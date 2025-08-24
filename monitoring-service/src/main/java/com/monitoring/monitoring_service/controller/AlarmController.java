package com.monitoring.monitoring_service.controller;

import com.monitoring.monitoring_service.model.AlarmEntity;
import com.monitoring.monitoring_service.repository.AlarmRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

/**
 * Professional AlarmController provides endpoints to view alarm history
 * with optional filtering, paging, and sorting.
 *
 * @since Aug, 2025
 * @author s Bostan
 */
@RestController
@RequestMapping("/alarms")
@Tag(name = "Alarms", description = "Endpoints for viewing alarm history with filters, paging, and sorting")
public class AlarmController {

    private final AlarmRepository alarmRepository;

    public AlarmController(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    /**
     * Filters and retrieves alarms based on severity and/or time range.
     * <p>
     * This endpoint supports the following filtering options:
     * <ul>
     *   <li>By severity only</li>
     *   <li>By time range only (from, to)</li>
     *   <li>By both severity and time range</li>
     *   <li>If no filter is provided, all alarms are returned</li>
     * </ul>
     *
     * @param severity Alarm severity (e.g., CRITICAL, WARNING, INFO)
     * @param from Start of the time range (ISO DateTime format)
     * @param to End of the time range (ISO DateTime format)
     * @param pageable Pagination information (page, size, sort)
     * @return A paginated list of alarms that match the filter criteria
     */
    @Operation(
            summary = "Filter alarms",
            description = "Search alarms by severity and/or time range. "
                    + "If no parameters are provided, all alarms will be returned.",
            tags = {"Alarms"}
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved alarms")
    @GetMapping("/filter")
    public Page<AlarmEntity> getAlarms(
            @Parameter(description = "Alarm severity (CRITICAL, WARNING, INFO)", example = "CRITICAL")
            @RequestParam(required = false) String severity,

            @Parameter(description = "Start of the time range in ISO format", example = "2025-08-23T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @Parameter(description = "End of the time range in ISO format", example = "2025-08-23T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,

            @Parameter(hidden = true) Pageable pageable
    ) {
        if (severity != null && from != null && to != null) {
            return alarmRepository.findBySeverityAndTimestampBetween(severity, from, to, pageable);
        } else if (severity != null) {
            return alarmRepository.findBySeverity(severity, pageable);
        } else if (from != null && to != null) {
            return alarmRepository.findByTimestampBetween(from, to, pageable);
        } else {
            return alarmRepository.findAll(pageable);
        }
    }
}
