package com.goktug.kafka;

import tools.jackson.databind.ObjectMapper;
import com.goktug.dto.ProductViewEvent;
import com.goktug.models.ProductView;
import com.goktug.repository.ProductViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewConsumer {

    private final ProductViewRepository productViewRepository;
    private final ObjectMapper objectMapper;

    // "product-views" topic'ini dinle; groupId ile hangi consumer grubuna ait olduğunu belirt.
    // Aynı groupId'ye sahip birden fazla consumer olsaydı Kafka mesajları aralarında bölerdi.
    // Acknowledgment parametresi: manuel ACK için Spring Kafka'nın bize verdiği "onay düğmesi".
    // application.properties'te ack-mode=manual olduğu için bu parametre inject edilebiliyor.
    @KafkaListener(topics = "product-views", groupId = "recommendation-group")
    public void consume(String message, Acknowledgment acknowledgment) {
        try {
            // Kafka'dan gelen ham JSON string'i Java nesnesine (DTO) dönüştür.
            ProductViewEvent event = objectMapper.readValue(message, ProductViewEvent.class);

            // Kafka "at least once" garantisi verir: ağ hatası ya da yeniden başlatma durumunda
            // aynı mesaj 2 kez gelebilir. messageId ile kontrol edip tekrar kaydetmiyoruz (idempotency).
            if (productViewRepository.existsByMessageId(event.getMessageid())) {
                log.warn("Duplicate event skipped: {}", event.getMessageid());
                // Duplicate olsa da ACK gönder; yoksa Kafka bu mesajı sonsuza kadar tekrar gönderir.
                acknowledgment.acknowledge();
                return;
            }

            ProductView productView = new ProductView();
            productView.setEvent(event.getEvent());
            productView.setMessageId(event.getMessageid());
            productView.setUserId(event.getUserid());
            productView.setProductId(event.getProperties().getProductid());
            productView.setSource(event.getContext().getSource());
            productView.setViewDate(LocalDateTime.now());
            productView.setIsDeleted(false);

            productViewRepository.save(productView);

            // Veritabanına başarıyla kaydedildikten SONRA ACK gönder.
            // Manuel ACK'in amacı budur: "işim bitmeden okundu deme".
            // Eğer save() patlarsa buraya gelinmez → catch'e düşer → ACK gönderilmez
            // → Kafka mesajı tekrar gönderir → veri kaybolmaz.
            acknowledgment.acknowledge();
            log.info("Saved view: user={}, product={}", event.getUserid(), event.getProperties().getProductid());

        } catch (Exception e) {
            // acknowledge() çağrılmadı → Kafka bu mesajın offset'ini ilerletmez → tekrar dener.
            log.error("Error consuming message: {}", e.getMessage());
        }
    }
}
