package com.anemona.kafka_alert_consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableAutoConfiguration
@EnableKafka
public class KafkaAlertConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaAlertConsumerApplication.class, args);
	}

}
