package com.anemona.kafka_alert_consumer.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class AlertaDTO {
    private Long id_alerta;
    private String descripcion_alerta;
    private int nivel_alerta;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha_alerta;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime hora_alerta;
    
    private Long id_paciente;
    private boolean visto;
    private Long id_estado_vital;
    private String parametro_alterado;
}
