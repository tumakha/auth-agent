package com.ldap.auth.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.util.Assert.isTrue;

/**
 * @author Yuriy Tumakha
 */
@Configuration
public class QuartzConfig {

    private static final long START_DELAY = 3000; // milliseconds
    private static final Pattern SYNC_INTERVAL_PATTERN = Pattern.compile("^(\\d+)(.+)");

    @Autowired
    private AppConfig appConfig;

    @Bean
    public MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean() {
        MethodInvokingJobDetailFactoryBean obj = new MethodInvokingJobDetailFactoryBean();
        obj.setTargetBeanName("syncTask");
        obj.setTargetMethod("sync");
        obj.setConcurrent(false);
        return obj;
    }

    @Bean
    public SimpleTriggerFactoryBean simpleTriggerFactoryBean(){
        SimpleTriggerFactoryBean stFactory = new SimpleTriggerFactoryBean();
        stFactory.setJobDetail(methodInvokingJobDetailFactoryBean().getObject());
        stFactory.setStartDelay(START_DELAY);
        stFactory.setRepeatInterval(getSyncInterval());
        return stFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(simpleTriggerFactoryBean().getObject());
        return scheduler;
    }

    private long getSyncInterval() {
        String syncInterval = appConfig.getSyncInterval();
        Matcher matcher = SYNC_INTERVAL_PATTERN.matcher(syncInterval);
        isTrue(matcher.matches(), "Property 'sync.interval' should starts with number");
        long number = Long.valueOf(matcher.group(1));
        String timeUnit = matcher.group(2).trim();
        isTrue(timeUnit.length() > 0, "Property 'sync.interval' should have time unit");

        long milliseconds = 1000; // second
        switch (timeUnit.toLowerCase().charAt(0)) {
            case 'd': milliseconds = 24L * 60 * 60 * 1000;
                      break;
            case 'h': milliseconds = 60L * 60 * 1000;
                      break;
            case 'm': milliseconds = 60L * 1000;
        }
        return number * milliseconds;
    }

}