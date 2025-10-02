package com.monitoring.monitoring_service.controller;

import com.monitoring.monitoring_service.dto.AlarmKpiDto;
import com.monitoring.monitoring_service.service.AlarmKpiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AlarmKpiController provides summarized alarm statistics.
 *
 * @since Aug, 2025
 */
@Slf4j
@RestController
@RequestMapping("/alarms/kpi")
@Tag(name = "Alarms KPI", description = "Endpoints for summarized alarm statistics")
public class AlarmKpiController {

    private final AlarmKpiService kpiService;

    public AlarmKpiController(AlarmKpiService kpiService) {
        this.kpiService = kpiService;
    }

    @Operation(
            summary = "Get alarm KPI",
            description = "Returns summarized statistics of alarms, including total and severity counts"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved KPI")
    @GetMapping
    public AlarmKpiDto getAlarmKpi() {
        log.debug("traceId={} | GET /alarms/kpi called", MDC.get("traceId"));
        try {
            AlarmKpiDto kpi = kpiService.calculateKpi();
            log.info("traceId={} | Successfully calculated alarm KPI: totalAlarms={}, counts={}", MDC.get("traceId") , kpi.getTotalAlarms(), kpi.getSeverityCounts());
            return kpi;
        } catch (Exception e) {
            log.error("traceId={} | Error calculating alarm KPI: {}", MDC.get("traceId") , e.getMessage(), e);
            throw e;
        }
    }
}
