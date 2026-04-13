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

// Dosyadan event okuyup Kafka'ya gonderir
@Service
@Slf4j
public class ViewProducer {

    // Kafka'ya mesaj gonderimi
    private final KafkaTemplate<String, String> kafkaTemplate;

    // Event dosya yolu (application.properties)
    @Value("${app.product-views-path}")
    private String filePath;

    // Hedef topic
    private static final String TOPIC = "product-views";

    public ViewProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Uygulama hazir oldugunda arka planda yayin baslatir
    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void startProducing() {
        try {
            // 1) Dosyadaki event satirlarini oku
            List<String> lines = Files.readAllLines(Path.of(filePath));

            log.info("Starting to publish {} events to Kafka", lines.size());

            // 2) Her satiri sirayla gonder
            for (String line : lines) {
                // Bos satiri atla
                if (line.isBlank())
                    continue;

                // 3) Event'e timestamp ekle
                String eventWithTimestamp = addTimestamp(line);

                // 4) Kafka'ya asenkron gonder
                kafkaTemplate.send(TOPIC, eventWithTimestamp)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.error("Failed to send event: {}", ex.getMessage());
                            } else {
                                log.info("Published event to Kafka: offset={}", result.getRecordMetadata().offset());
                            }
                        });

                // 5) Yayini yavaslat (1 sn)
                Thread.sleep(1000);
            }

            log.info("All events published successfully");

        } catch (Exception e) {
            // Hata durumunda logla
            log.error("Error publishing events: {}", e.getMessage());
        }
    }

    // JSON sonuna timestamp ekler
    private String addTimestamp(String json) {
        String timestamp = LocalDateTime.now().toString();
        return json.substring(0, json.length() - 1) + ", \"timestamp\": \"" + timestamp + "\"}";
    }
}