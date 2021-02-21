package org.codingsquid.consumer.config;

import lombok.extern.slf4j.Slf4j;
import org.codingsquid.core.EnableCore;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;

/**
 * @author taewoong.han
 * @since 2021.02.21
 */
@Slf4j
@Configuration
@EnableCore
@EnableKafka
public class SpringKafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(ConsumerFactory<String, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(false);
        factory.getContainerProperties().setCommitCallback((offsets, exception) -> {
            if (exception == null) {
                log.info("---- success commit ----");
                offsets.forEach((topicPartition, offsetAndMetadata) -> {
                    log.info("topic: {}", topicPartition.topic());
                    log.info("partition: {}", topicPartition.partition());
                    log.info("offset: {}", offsetAndMetadata.offset());
                });
                log.info("------------------------");
            }
        });
        factory.getContainerProperties().setCommitRetries(3);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }
}
