package org.codingsquid.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.codingsquid.core.config.JacksonConfig;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author taewoong.han
 * @since 2021.02.12
 */
@Slf4j
public class ConsumerApplication {
    private static final AtomicBoolean flag = new AtomicBoolean(true);

    public static void main(String... args) throws JsonProcessingException, InterruptedException {
        ObjectMapper objectMapper = new JacksonConfig().objectMapper();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps());
        List<String> topics = new ArrayList<>();
        topics.add("play.kafka");

        ConsumerRebalanceListener rebalanceListener = new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                log.info("onPartitionsRevoked: {}", partitions);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                log.info("onPartitionsAssigned: {}", partitions);
            }
        };

        consumer.subscribe(topics, rebalanceListener);

        while (flag.get()) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3)); //3초동안 읽을 데이터를 계속 fetch하다가 없으면 empty collection을 return

            for (ConsumerRecord<String, String> record : records) {
                Map<String, String> value = objectMapper.readValue(record.value(), new TypeReference<>() {});
                log.info("message: {}", value);

                Optional.ofNullable(value.get("stop"))
                        .ifPresent(isStop -> {
                            if (Boolean.parseBoolean(isStop)) {
                                flag.set(false);
                            }
                        });
            }

            log.info("---- batch read message ----");
            consumer.commitSync();
        }

        consumer.close();
    }

    private static Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "vanilla.consumer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1000);

        return props;
    }
}
