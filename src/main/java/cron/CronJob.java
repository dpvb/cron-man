package cron;

import exceptions.CronParseException;

public class CronJob {

    /**
     * CRON EXPLANATION:
     * * * * * *
     * | | | | |
     * | | | | day of the week (0-6) (Sunday to Saturday)
     * | | | month (1-12)
     * | | day of the month (1-31)
     * | hour (0-23)
     * minute (0-59)
     */
    private String cronExpression;
    private Runnable runnable;

    private CronTime minute;
    private CronTime hour;
    private CronTime dayOfTheMonth;
    private CronTime month;
    private CronTime dayOfTheWeek;

    public CronJob(String cronExpression, Runnable runnable) {
        this.cronExpression = cronExpression;
        this.runnable = runnable;
        parseCronExpression();
    }

    private void parseCronExpression() {
        String[] components = cronExpression.split(" ");
        if (components.length != 5) {
            throw new CronParseException("Cron Expression should have 5 parts: * * * * *");
        }

        this.minute = new CronTime(components[0], TimeType.MINUTE);
        this.hour = new CronTime(components[1], TimeType.HOUR);
        this.dayOfTheMonth = new CronTime(components[2], TimeType.DAY_OF_MONTH);
        this.month = new CronTime(components[3], TimeType.MONTH);
        this.dayOfTheWeek = new CronTime(components[4], TimeType.DAY_OF_MONTH);
    }

    CronTime getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    CronTime getMonth() {
        return month;
    }

    CronTime getDayOfTheMonth() {
        return dayOfTheMonth;
    }

    CronTime getHour() {
        return hour;
    }

    CronTime getMinute() {
        return minute;
    }

    protected Runnable getRunnable() {
        return runnable;
    }

}
