package com.monitoring.monitoring_service.controller;

import com.monitoring.monitoring_service.model.AlarmEntity;
import com.monitoring.monitoring_service.repository.AlarmRepository;
import io.swagger.v3.oas.annotations.Operation;
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
     * Get alarms with optional filters, paging, and sorting.
     *
     * @param severity Optional: WARNING or CRITICAL
     * @param from Optional: start of time range (ISO format)
     * @param to Optional: end of time range (ISO format)
     * @param page Optional: page number (default 0)
     * @param size Optional: page size (default 20)
     * @param sortBy Optional: field to sort by (default "timestamp")
     * @param direction Optional: ASC or DESC (default DESC)
     * @return Page of AlarmEntity
     */
    @Operation(
            summary = "Get alarms with filters, paging, and sorting",
            description = "Retrieves alarms with optional severity/time filters, paged results, and sorting."
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved alarms")
    @GetMapping("/filter")
    public Page<AlarmEntity> getAlarmsAdvanced(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        // Sorting configuration
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Build example for filtering by severity
        AlarmEntity probe = new AlarmEntity();
        if (severity != null && !severity.isEmpty()) {
            probe.setSeverity(severity.toUpperCase());
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id", "serviceName", "metricName", "value", "timestamp", "message");

        Example<AlarmEntity> example = Example.of(probe, matcher);

        Page<AlarmEntity> alarms = alarmRepository.findAll(example, pageable);

        // Filter by time range manually (since Example cannot filter LocalDateTime)
        if (from != null || to != null) {
            alarms = new PageImpl<>(
                    alarms.stream()
                            .filter(a -> (from == null || !a.getTimestamp().isBefore(from)) &&
                                    (to == null || !a.getTimestamp().isAfter(to)))
                            .toList(),
                    pageable,
                    alarms.getTotalElements()
            );
        }

        return alarms;
    }
}