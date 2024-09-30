//package com.rajsubhod.authservice.kafka;
//
//import com.rajsubhod.authservice.dto.UserInfoDto;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class config {
//
//    @Bean
//    public ProducerFactory<String, UserInfoDto> producerFactory() {
//        Map<String,Object> props = new HashMap<>();
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "com.rajsubhod.authservice.serializer.UserInfoDtoSerializer");
//        return new DefaultKafkaProducerFactory<>(props);
//    }
//
//    @Bean
//    public KafkaTemplate<String,UserInfoDto> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//}
