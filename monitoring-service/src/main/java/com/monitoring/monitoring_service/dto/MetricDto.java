package com.monitoring.monitoring_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created on Aug, 2025
 *
 * @author s Bostan
 */
@AllArgsConstructor
@Getter
@Setter
public class MetricDto {
    private Long id;
    private String name;
    private Double value;
}
