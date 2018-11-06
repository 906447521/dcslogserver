package cn.dcs.etoe;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class JobScheduler {

	private static Logger logger = Logger.getLogger(JobScheduler.class);

	private static final String QUARTZ_EXPRESSION = "warn.quartz.expression";

	private Scheduler scheduler;

	private static JobScheduler timer;

	public static JobScheduler instance() {
		if (timer == null) {
			timer = new JobScheduler();
			try {
				SchedulerFactory schedulerFactory = new StdSchedulerFactory();
				timer.scheduler = schedulerFactory.getScheduler();
			} catch (SchedulerException e) {
				logger.error(e);
			}
		}
		return timer;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void shutdown() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
		}
	}

	public void start() {

		try {
			Configuration _conf = new Configuration();
			SqliteHelper.init();

			String expression  = _conf.get(QUARTZ_EXPRESSION);
			
			if(expression == null || expression.equals("")) {
				logger.warn("scheduler can not start. ");
				return;
			}
			
			JobDetail jobdetail = new JobDetail();
			CronTrigger trigger = new CronTrigger();
			jobdetail.setName("worker-1");
			jobdetail.setVolatility(true);
			jobdetail.setJobClass(JobRunnable.class);
			jobdetail.setDurability(true);
			trigger.setName("worker-trigger-1");
			trigger.setCronExpression(expression);
			scheduler.scheduleJob(jobdetail, trigger);
			scheduler.start();
			logger.warn("scheduler start.");
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
