package com.goktug.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ViewProducer {

    // KafkaTemplate: Kafka'ya mesaj göndermek için Spring'in hazır aracı.
    // <String, String> → key tipi, value tipi. Burada her ikisi de düz JSON string.
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Mesaj dosyasının yolunu application.properties'ten okur (hard-code etmemek için).
    @Value("${app.product-views-path}")
    private String filePath;

    // Mesajların gönderileceği Kafka topic'inin adı.
    private static final String TOPIC = "product-views";

    public ViewProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // @Async: Bu metodu ayrı bir thread'de çalıştır; uygulama başlangıcını bloklamasın.
    // @EventListener(ApplicationReadyEvent.class): Uygulama tamamen ayağa kalktığında tetiklenir.
    // (Kafka bağlantısı hazır olmadan mesaj göndermemek için ApplicationReadyEvent tercih edildi.)
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void startProducing() {
        try {
            List<String> lines = Files.readAllLines(Path.of(filePath));

            log.info("Starting to publish {} events to Kafka", lines.size());

            for (String line : lines) {
                if (line.isBlank())
                    continue;

                String eventWithTimestamp = addTimestamp(line);

                // kafkaTemplate.send() asenkrondur; mesajı Kafka broker'a iletir ve hemen döner.
                // .whenComplete() ile gönderim sonucu (başarı/hata) callback olarak yakalanır.
                kafkaTemplate.send(TOPIC, eventWithTimestamp)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.error("Failed to send event: {}", ex.getMessage());
                            } else {
                                // offset: Kafka'nın bu mesaja atadığı sıra numarası (konumu).
                                log.info("Published event to Kafka: offset={}", result.getRecordMetadata().offset());
                            }
                        });

                // Consumer'ın işleyebileceğinden hızlı mesaj göndermemek için 1 sn bekle.
                Thread.sleep(1000);
            }

            log.info("All events published successfully");

        } catch (Exception e) {
            log.error("Error publishing events: {}", e.getMessage());
        }
    }

    // JSON'ın kapanış } karakterini kaldırıp timestamp ekler, sonra } ile kapatır.
    // Örnek: {"event":"view"} → {"event":"view", "timestamp": "2024-..."}
    private String addTimestamp(String json) {
        String timestamp = LocalDateTime.now().toString();
        return json.substring(0, json.length() - 1) + ", \"timestamp\": \"" + timestamp + "\"}";
    }
}
