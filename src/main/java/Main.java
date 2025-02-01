import cron.CronJob;
import cron.CronScheduler;

public class Main {

    public static void main(String[] args) {
        CronJob cronJob = new CronJob("*/2 * * * *", () -> System.out.println("hello"));
        CronScheduler scheduler = CronScheduler.getInstance();
        scheduler.schedule(cronJob);
    }

}
