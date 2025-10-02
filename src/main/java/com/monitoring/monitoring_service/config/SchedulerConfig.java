package com.monitoring.monitoring_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SchedulerConfig is responsible for enabling Spring's scheduled tasks.
 * This configuration allows methods annotated with @Scheduled in the project
 * to run periodically according to their defined intervals.
 *
 * @since  Aug, 2025
 * @author s Bostan
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
}
