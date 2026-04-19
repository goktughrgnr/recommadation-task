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

    @KafkaListener(topics = "product-views", groupId = "recommendation-group")
    public void consume(String message, Acknowledgment acknowledgment) {
        try {
            ProductViewEvent event = objectMapper.readValue(message, ProductViewEvent.class);

            if (productViewRepository.existsByMessageId(event.getMessageid())) {
                log.warn("Duplicate event skipped: {}", event.getMessageid());
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
            acknowledgment.acknowledge();
            log.info("Saved view: user={}, product={}", event.getUserid(), event.getProperties().getProductid());

        } catch (Exception e) {
            log.error("Error consuming message: {}", e.getMessage());
        }
    }
}
