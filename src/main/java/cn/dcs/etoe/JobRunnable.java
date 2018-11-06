package cn.dcs.etoe;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import cn.dcs.Log;

public class JobRunnable implements Job {

	private static final Logger logger = Logger.getLogger(JobRunnable.class);

	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		long s = System.currentTimeMillis();

		Configuration conf = new Configuration();

		conf.initStart(new Date());

		EtldbSelector search = new EtldbSelector();

		SqliteHelper sqlite = new SqliteHelper();

		try {

			List<String> tables = search.getTables();

			for (String table : tables) {

				if (table.indexOf("pool") != -1 && table.indexOf("182_138_0_174") != -1 && table.endsWith(conf.day)) {

					search.doSearchPool(table, conf, sqlite);

					if (logger.isDebugEnabled()) {

						logger.debug("search table" + table);

					}

				}

			}

		} catch (Exception e) {

			Log.error("warn error", e);

		}

		sqlite.release();

		search.release();

		if (logger.isDebugEnabled()) {

			logger.debug("warn use " + (System.currentTimeMillis() - s) + " ms, start:" + conf.start + ", end:"
					+ conf.end + ", day:" + conf.day);
		}

	}
}
