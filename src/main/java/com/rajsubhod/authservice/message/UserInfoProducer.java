package com.rajsubhod.authservice.message;

import com.rajsubhod.authservice.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoProducer {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoProducer.class);
    private final KafkaTemplate<String, UserInfoDto> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String TOPIC_NAME;

    public void sendEventTOQueue(UserInfoDto userInfoDto) {
        logger.info("Sending message to topic: {}", TOPIC_NAME);
        Message<UserInfoDto> message = MessageBuilder.withPayload(userInfoDto)
                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                .build();
        kafkaTemplate.send(message);
    }

}
