package com.anemona.kafka_alert_consumer.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import com.anemona.kafka_alert_consumer.dto.AlertaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class KafkaConsumerConfig {
    
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ConsumerFactory<String, AlertaDTO> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-1:9092,kafka-2:9093,kafka-3:9094");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "anemona_alert_group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // Configuración del JsonDeserializer
        Map<String, Object> deserializerProps = new HashMap<>();
        deserializerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        deserializerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, AlertaDTO.class.getName());
        deserializerProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        
        // Creamos el JsonDeserializer con nuestro ObjectMapper personalizado
        JsonDeserializer<AlertaDTO> jsonDeserializer = new JsonDeserializer<>(AlertaDTO.class, objectMapper);
        jsonDeserializer.configure(deserializerProps, false);
        
        // Creamos el ConsumerFactory con el deserializador configurado
        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            jsonDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AlertaDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AlertaDTO> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler(
            new FixedBackOff(1000L, 3L)
        ));
        return factory;
    }
}