package com.monitoring.monitoring_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * AlarmKpiDto represents summarized alarm statistics.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@Data
@AllArgsConstructor
public class AlarmKpiDto {
    private long totalAlarms;
    private Map<String, Long> severityCounts; // WARNING -> count, CRITICAL -> count
}
