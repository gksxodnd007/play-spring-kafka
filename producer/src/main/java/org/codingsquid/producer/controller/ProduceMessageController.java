package org.codingsquid.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author taewoong.han
 * @since 2021.02.21
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class ProduceMessageController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @PostMapping(path = "/api/v1/produce-message")
    public ResponseEntity<Void> produceMessage(@RequestBody Map<String, String> body) throws JsonProcessingException {
        body.forEach((key, value) -> log.info("key: {}, value: {}", key, value));
        String message = objectMapper.writeValueAsString(body);
        log.info("message: " + message);
        ListenableFuture<SendResult<String, String>> kafkaSendFuture = kafkaTemplate.send("play.kafka", message);
        kafkaSendFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("produce message fail!! msg: {}", ex.getMessage(), ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("ProducerRecord: {}", result.getProducerRecord());
                result.getProducerRecord()
                        .headers()
                        .forEach(header -> log.info("header: {}", header));
                log.info("RecordMetadata: {}", result.getRecordMetadata());
            }
        });

        return ResponseEntity.noContent()
                .build();
    }
}
