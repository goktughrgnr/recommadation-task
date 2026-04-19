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

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.product-views-path}")
    private String filePath;

    private static final String TOPIC = "product-views";

    public ViewProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

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

                kafkaTemplate.send(TOPIC, eventWithTimestamp)
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                log.error("Failed to send event: {}", ex.getMessage());
                            } else {
                                log.info("Published event to Kafka: offset={}", result.getRecordMetadata().offset());
                            }
                        });

                Thread.sleep(1000);
            }

            log.info("All events published successfully");

        } catch (Exception e) {
            log.error("Error publishing events: {}", e.getMessage());
        }
    }

    private String addTimestamp(String json) {
        String timestamp = LocalDateTime.now().toString();
        return json.substring(0, json.length() - 1) + ", \"timestamp\": \"" + timestamp + "\"}";
    }
}
