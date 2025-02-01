package cron;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CronScheduler {

    private static CronScheduler INSTANCE;
    private final Set<CronJob> cronJobs;

    private CronScheduler() {
        this.cronJobs = new HashSet<>();
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(this::cronRunner, secondsToNextMinute(), 60L, TimeUnit.SECONDS);
    }

    public static CronScheduler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CronScheduler();
        }

        return INSTANCE;
    }

    public void schedule(CronJob cronJob) {
        this.cronJobs.add(cronJob);
    }

    private void cronRunner() {
        final CurrentTime currentTime = new CurrentTime(ZonedDateTime.now(ZoneId.of("UTC")));
        cronJobs.stream()
                .filter(cronJob -> evaluateCron(cronJob, currentTime))
                .forEach(cronJob -> CompletableFuture.runAsync(cronJob.getRunnable()));
    }

    private boolean evaluateCron(CronJob cron, CurrentTime currentTime) {
        boolean minuteMatch = cron.getMinute().evaluate(currentTime.minute);
        boolean hourMatch = cron.getHour().evaluate(currentTime.hour);
        boolean dayOfTheMonthMatch = cron.getDayOfTheMonth().evaluate(currentTime.dayOfTheMonth);
        boolean monthMatch = cron.getMonth().evaluate(currentTime.month);
        boolean dayOfTheWeek = cron.getDayOfTheWeek().evaluate(currentTime.dayOfTheWeek);

        return minuteMatch && hourMatch && dayOfTheMonthMatch && monthMatch && dayOfTheWeek;
    }

    private static long secondsToNextMinute() {
        return 60 - LocalDateTime.now().getSecond();
    }

    private static class CurrentTime {

        private final int minute;
        private final int hour;
        private final int dayOfTheMonth;
        private final int month;
        private final int dayOfTheWeek;

        public CurrentTime(ZonedDateTime time) {
            this.minute = time.getMinute();
            this.hour = time.getHour();
            this.dayOfTheMonth = time.getDayOfMonth();
            this.month = time.getMonthValue();
            this.dayOfTheWeek = time.getDayOfWeek().getValue() % 7;
        }
    }

}
