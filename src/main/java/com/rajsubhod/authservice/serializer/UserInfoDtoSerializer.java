package com.rajsubhod.authservice.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajsubhod.authservice.dto.UserInfoDto;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class UserInfoDtoSerializer implements Serializer<UserInfoDto> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, UserInfoDto userInfoDto) {
        byte[] stream = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            stream = objectMapper.writeValueAsString(userInfoDto).getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error when serializing UserInfoDto to byte[]");
        }
        return stream;
    }


    @Override
    public void close() {
        Serializer.super.close();
    }
}
