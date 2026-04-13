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

/**
 * Kafka'dan gelen product view event'lerini okuyup veritabanina kaydeder.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ViewConsumer {

    // DB islemleri
    private final ProductViewRepository productViewRepository;

    // JSON -> DTO donusumu
    private final ObjectMapper objectMapper;

    // product-views topic'ini recommendation-group ile dinler
    @KafkaListener(topics = "product-views", groupId = "recommendation-group")
    // Mesaji isler, basariliysa offset commit eder
    public void consume(String message, Acknowledgment acknowledgment) {
        try {
            // 1) JSON mesaji DTO'ya cevir
            ProductViewEvent event = objectMapper.readValue(message, ProductViewEvent.class);

            // 2) Ayni messageId tekrar islenmesin
            if (productViewRepository.existsByMessageId(event.getMessageid())) {
                log.warn("Duplicate event skipped: {}", event.getMessageid());
                acknowledgment.acknowledge();
                return;
            }

            // 3) DTO verisini entity'e map et
            ProductView productView = new ProductView();
            productView.setEvent(event.getEvent());
            productView.setMessageId(event.getMessageid());
            productView.setUserId(event.getUserid());
            productView.setProductId(event.getProperties().getProductid());
            productView.setSource(event.getContext().getSource());
            productView.setViewDate(LocalDateTime.now());
            productView.setIsDeleted(false);

            // 4) Kaydet
            productViewRepository.save(productView);

            // 5) Basariliysa commit et
            acknowledgment.acknowledge();
            log.info("Saved view: user={}, product={}", event.getUserid(), event.getProperties().getProductid());

        } catch (Exception e) {
            // Hata durumunda logla
            log.error("Error consuming message: {}", e.getMessage());
        }
    }
}