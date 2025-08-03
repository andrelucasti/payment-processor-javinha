package io.andrelucas.Rinha;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;


@EnableScheduling
@Configuration
public class WorkerConfiguration implements SchedulingConfigurer {

    @Override
    public void configureTasks(final ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(4);
        taskScheduler.setThreadNamePrefix("rinha-worker-");
        taskScheduler.setVirtualThreads(true);
        taskScheduler.initialize();
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setAwaitTerminationSeconds(10);
        taskRegistrar.setScheduler(taskScheduler);
    }
   
}
