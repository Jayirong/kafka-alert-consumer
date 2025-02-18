package com.anemona.kafka_alert_consumer.service;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anemona.kafka_alert_consumer.dto.AlertaDTO;

@Service
public class KafkaAlertConsumer {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String ALERTA_ENDPOINT = "http://aneback:8080/api/alertas/ingreso/";

    @KafkaListener(topics = "alertas", groupId = "anemona_alert_group")
    public void listen(AlertaDTO alerta) {

        try{
            System.out.println("ALERTA RESIVIDAAAAAAASCASDASDA: " + alerta);

            //seteamos hora y fgecha
            alerta.setFecha_alerta(LocalDate.now());
            alerta.setHora_alerta(LocalTime.now());

            //url con id del estado, el 1 es para pruebas
            String url = ALERTA_ENDPOINT + "1";

            //configuramos headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            //request
            HttpEntity<AlertaDTO> request = new HttpEntity<>(alerta, headers);

            //enviar alerta a aneback
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            System.out.println("ALERTA ENBIADAS CON EKSITOOOO DEAAA: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("WATEFOK NO FUNSIONÓOOOO QUE ONDA LOLñÑ: " + e.getMessage());
        }

    }

}
