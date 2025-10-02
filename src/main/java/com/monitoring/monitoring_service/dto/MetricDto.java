package com.monitoring.monitoring_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @since Aug, 2025
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
