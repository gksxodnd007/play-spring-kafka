package org.codingsquid.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @author taewoong.han
 * @since 2021.02.21
 */
@Slf4j
@Component
public class SpringKafkaConsumer {

    @KafkaListener(id = "play.kafka.consumer", topics = "play.kafka", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecords<String, String> records, Acknowledgment acknowledgment) {
        for (ConsumerRecord<String, String> record : records) {
            log.info("message: {}", record.value());
        }
        acknowledgment.acknowledge();
        log.info("---- batch read message ----");
    }
}
