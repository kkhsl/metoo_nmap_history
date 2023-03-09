package com.metoo.nspm.core.config.shiro.cache;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

//@Configuration
//public class JsonConfig {
//
//        @Bean
//        public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
//            ObjectMapper objectMapper = new ObjectMapper();
//            // 反序列化的时候 如果加了其他未知的属性, 不抛异常
//            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//            // 序列化的时候  如果是空对象, 不抛出异常
//            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//            return new MappingJackson2HttpMessageConverter(objectMapper);
//        }
//}
