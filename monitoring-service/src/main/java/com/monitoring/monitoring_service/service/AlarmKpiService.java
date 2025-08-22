package com.monitoring.monitoring_service.service;

import com.monitoring.monitoring_service.dto.AlarmKpiDto;
import com.monitoring.monitoring_service.model.AlarmEntity;
import com.monitoring.monitoring_service.repository.AlarmRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AlarmKpiService calculates summarized statistics for alarms.
 *
 * @since Aug, 2025
 */
@Service
public class AlarmKpiService {

    private final AlarmRepository alarmRepository;

    public AlarmKpiService(AlarmRepository alarmRepository) {
        this.alarmRepository = alarmRepository;
    }

    public AlarmKpiDto calculateKpi() {
        List<AlarmEntity> alarms = alarmRepository.findAll();

        long total = alarms.size();
        Map<String, Long> counts = alarms.stream()
                .collect(Collectors.groupingBy(AlarmEntity::getSeverity, Collectors.counting()));

        return new AlarmKpiDto(total, counts);
    }
}
