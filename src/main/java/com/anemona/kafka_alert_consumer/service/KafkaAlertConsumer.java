package com.anemona.kafka_alert_consumer.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.anemona.kafka_alert_consumer.dto.AlertaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class KafkaAlertConsumer {

    private final RestTemplate restTemplate = new RestTemplate();
    // El endpoint de aneback; el id que va en la URL es el id del paciente (para pruebas usamos "1")
    private final String ALERTA_ENDPOINT = "http://aneback:8080/api/alertas/ingreso/";

    private final ObjectMapper objectMapper;

    public KafkaAlertConsumer() {
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "alertas", groupId = "anemona_alert_group")
    public void listen(AlertaDTO alerta) {
        try {
            System.out.println("ALERTA RECIBIDA: " + alerta);

            // Para este flujo de pruebas, omitiremos enviar fecha y hora (aneback se encarga de asignarlas)
            // Y forzamos que el estado vital que referenciamos sea 1 (debe existir en aneback)
            Map<String, Object> payload = new HashMap<>();
            payload.put("descripcion_alerta", alerta.getDescripcion_alerta());
            payload.put("nivel_alerta", alerta.getNivel_alerta());
            payload.put("parametro_alterado", alerta.getParametro_alterado());
            payload.put("visto", alerta.isVisto());
            // No enviamos fecha ni hora, ni id_paciente (que se obtiene de la URL en aneback)
            // Construimos el objeto estadoVital con un id existente (1)
            Map<String, Object> estadoVitalMap = new HashMap<>();
            estadoVitalMap.put("id_estado", 1);
            payload.put("estadoVital", estadoVitalMap);

            // La URL usa el id del paciente; para pruebas usamos "1"
            String url = ALERTA_ENDPOINT + "1";

            // Configuramos los headers y convertimos el payload a JSON
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String jsonPayload = objectMapper.writeValueAsString(payload);
            System.out.println("JSON enviado a aneback: " + jsonPayload);

            // Preparamos la solicitud HTTP y la enviamos
            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            System.out.println("ALERTA ENVIADA CON ÉXITO, ERÍ TERRIBLE DE BACÁN LOCO: " + response.getStatusCode());

        } catch (Exception e) {
            System.err.println("Error en deserialización o procesamiento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
