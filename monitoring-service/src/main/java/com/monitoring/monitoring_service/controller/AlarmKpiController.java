package com.monitoring.monitoring_service.controller;

import com.monitoring.monitoring_service.dto.AlarmKpiDto;
import com.monitoring.monitoring_service.service.AlarmKpiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AlarmKpiController provides summarized alarm statistics.
 *
 * @since Aug, 2025
 */
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
        return kpiService.calculateKpi();
    }
}
