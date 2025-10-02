package com.monitoring.monitoring_service.service;

import com.monitoring.monitoring_service.dto.AlarmKpiDto;
import com.monitoring.monitoring_service.model.AlarmEntity;
import com.monitoring.monitoring_service.repository.AlarmRepository;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AlarmKpiService calculates summarized statistics for alarms.
 *
 * @since Aug, 2025
 */
@Slf4j
@Service
public class AlarmKpiService {

    private final AlarmRepository alarmRepository;

    public AlarmKpiService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public AlarmKpiDto calculateKpi() {
        log.debug("Starting KPI calculation for alarms...");

        try {
            // Fetch all alarms from repository
            List<AlarmEntity> alarms = alarmRepository.findAll();
            log.info("Fetched {} alarms from repository.", alarms.size());

            // Group alarms by severity and count
            Map<String, Long> counts = alarms.stream()
                    .collect(Collectors.groupingBy(AlarmEntity::getSeverity, Collectors.counting()));

            AlarmKpiDto kpiDto = new AlarmKpiDto(alarms.size(), counts);

            log.info("KPI calculation successful: totalAlarms={}, counts={}" ,kpiDto.getTotalAlarms(), kpiDto.getSeverityCounts());
            log.debug("KPI details: {}", kpiDto);

            return kpiDto;
        } catch (Exception e) {
            log.error("Error occurred while calculating alarm KPI: {}", e.getMessage(), e);
            throw e;
        }
    }


}
