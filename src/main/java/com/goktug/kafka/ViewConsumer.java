package com.goktug.kafka;

import tools.jackson.databind.ObjectMapper;
import com.goktug.dto.ProductViewEvent;
import com.goktug.service.ProductViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewConsumer {

    private final ProductViewService productViewService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "product-views", groupId = "recommendation-group")
    public void consume(String message, Acknowledgment acknowledgment) {
        try {
            ProductViewEvent event = objectMapper.readValue(message, ProductViewEvent.class);

            if (productViewService.isDuplicate(event.getMessageid())) {
                log.warn("Duplicate event skipped: {}", event.getMessageid());
                acknowledgment.acknowledge();
                return;
            }

            productViewService.processViewEvent(event);

            acknowledgment.acknowledge();
            log.info("Saved view: user={}, product={}", event.getUserid(), event.getProperties().getProductid());

        } catch (Exception e) {
            log.error("Error consuming message: {}", e.getMessage());
        }
    }
}
