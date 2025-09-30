package com.monitoring.monitoring_service;

import com.monitoring.monitoring_service.config.AlarmConfig;
import com.monitoring.monitoring_service.model.Alarm;
import com.monitoring.monitoring_service.model.Metric;
import com.monitoring.monitoring_service.model.AlarmEntity;
import com.monitoring.monitoring_service.notifier.Notifier;
import com.monitoring.monitoring_service.repository.AlarmRepository;
import com.monitoring.monitoring_service.repository.MetricRepository;
import com.monitoring.monitoring_service.service.AlarmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AlarmService}.
 *
 * <p>This test class verifies the behavior of AlarmService, including:
 * <ul>
 *     <li>Handling of empty metric lists</li>
 *     <li>Threshold evaluation for WARNING and CRITICAL levels</li>
 *     <li>Alarm dispatching to Notifiers</li>
 *     <li>Cooldown logic to prevent spamming alarms</li>
 *     <li>Persistence of AlarmEntity to the repository</li>
 * </ul>
 *
 * <p>Mockito is used to mock dependencies such as repositories and notifiers.
 *
 * @since Sep, 2025
 * @author s Bostan
 */
class AlarmServiceTest {

    private MetricRepository metricRepository;
    private AlarmRepository alarmRepository;
    private AlarmConfig alarmConfig;
    private Notifier notifier;
    private AlarmService alarmService;

    /**
     * Sets up mocks and AlarmService instance before each test.
     */
    @BeforeEach
    void setUp() {
        metricRepository = mock(MetricRepository.class);
        alarmRepository = mock(AlarmRepository.class);
        notifier = mock(Notifier.class);

        alarmConfig = mock(AlarmConfig.class);
        when(alarmConfig.getCpuThresholdWarning()).thenReturn(70);
        when(alarmConfig.getCpuThresholdCritical()).thenReturn(90);
        when(alarmConfig.getMemoryThresholdWarning()).thenReturn(70);
        when(alarmConfig.getMemoryThresholdCritical()).thenReturn(90);
        when(alarmConfig.getRequestsThresholdWarning()).thenReturn(1000);
        when(alarmConfig.getRequestsThresholdCritical()).thenReturn(2000);
        when(alarmConfig.getCooldownSeconds()).thenReturn(10);

        alarmService = new AlarmService(metricRepository, alarmConfig, List.of(notifier), alarmRepository);
    }

    /**
     * Tests that no alarm is triggered when the metric repository is empty.
     */
    @Test
    void testCheckMetricsForAlarms_NoMetrics() {
        when(metricRepository.findAll()).thenReturn(Collections.emptyList());

        alarmService.checkMetricsForAlarms();

        verify(alarmRepository, never()).save(any());
        verify(notifier, never()).notify(any());
    }

    /**
     * Tests that a WARNING level alarm is created when CPU value is above warning threshold
     * but below critical threshold.
     */
    @Test
    void testCheckMetricsForAlarms_WarningThreshold() {
        Metric metric = new Metric();
        metric.setServiceName("TestService");
        metric.setCpu(75); // between warning and critical
        metric.setMemory(50);
        metric.setRequests(500);
        metric.setTimestamp(LocalDateTime.now());

        when(metricRepository.findAll()).thenReturn(List.of(metric));
        when(alarmRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        alarmService.checkMetricsForAlarms();

        ArgumentCaptor<Alarm> alarmCaptor = ArgumentCaptor.forClass(Alarm.class);
        verify(notifier).notify(alarmCaptor.capture());
        Alarm alarm = alarmCaptor.getValue();

        assertEquals("WARNING", alarm.getSeverity());
        assertEquals("CPU", alarm.getMetricName());
        assertEquals(75, alarm.getValue());
        verify(alarmRepository).save(any(AlarmEntity.class));
    }

    /**
     * Tests that a CRITICAL level alarm is created when CPU value is above critical threshold.
     */
    @Test
    void testCheckMetricsForAlarms_CriticalThreshold() {
        Metric metric = new Metric();
        metric.setServiceName("TestService");
        metric.setCpu(95); // above critical
        metric.setMemory(50);
        metric.setRequests(500);
        metric.setTimestamp(LocalDateTime.now());

        when(metricRepository.findAll()).thenReturn(List.of(metric));
        when(alarmRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        alarmService.checkMetricsForAlarms();

        ArgumentCaptor<Alarm> alarmCaptor = ArgumentCaptor.forClass(Alarm.class);
        verify(notifier).notify(alarmCaptor.capture());
        Alarm alarm = alarmCaptor.getValue();

        assertEquals("CRITICAL", alarm.getSeverity());
        assertEquals("CPU", alarm.getMetricName());
        assertEquals(95, alarm.getValue());
        verify(alarmRepository).save(any(AlarmEntity.class));
    }

    /**
     * Tests that alarms are not sent when cooldown is active, preventing alarm spamming.
     */
    @Test
    void testCheckMetricsForAlarms_CooldownActive() {
        Metric metric = new Metric();
        metric.setServiceName("TestService");
        metric.setCpu(95);
        metric.setMemory(50);
        metric.setRequests(500);
        metric.setTimestamp(LocalDateTime.now());

        when(metricRepository.findAll()).thenReturn(List.of(metric));
        when(alarmRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // First alarm should trigger normally
        alarmService.checkMetricsForAlarms();
        verify(notifier).notify(any());

        // Immediately call again, should be skipped due to cooldown
        reset(notifier, alarmRepository); // reset mocks
        alarmService.checkMetricsForAlarms();
        verify(notifier, never()).notify(any());
        verify(alarmRepository, never()).save(any());
    }
}
