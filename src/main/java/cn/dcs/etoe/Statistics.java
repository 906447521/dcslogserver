package cn.dcs.etoe;

import java.util.Timer;
import java.util.TimerTask;

import cn.dcs.Log;

public class Statistics {

	static int warn;

	static int push;

	static int remove;

	public static void start() {

		Timer t = new Timer();

		t.schedule(new TimerTask() {

			public void run() {

				log();

			}

		}, 0, 60 * 60 * 1000);

	}

	public synchronized static void push() {
		push++;
	}

	public synchronized static void remove() {
		remove++;
	}

	public synchronized static void warn() {
		warn++;
	}

	private static void log() {
		Log.warn("warn : " + warn + ",push : " + push + ",remove : " + remove);
	}

}
