package com.pg.search.servlet;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.matrixone.apps.domain.util.ContextUtil;
import matrix.db.Context;
import matrix.db.JPO;

public class PGSearchCacheServlet extends HttpServlet {

	/**
	 * Platform Search 2015x.4 <br>
	 * Servlet to load all search related cache for search fields using chooser JPO - pgSearchUtils.getChooserValues
	 */
	private static final long serialVersionUID = -8144729107487262304L;
	private static final int nTimeToStartHOUR = 6;
	private static final int nTimeToStartMIN = 00;
	private static final int nTimeToStartAMPM = Calendar.PM;
	private static final long lPicklistCacheReloadPeriodInMS = 300000; 	//5 minutes
	private static final Logger logger = Logger.getLogger(PGSearchCacheServlet.class.getName());

	@SuppressWarnings("deprecation")
	public void init(ServletConfig paramServletConfig) throws ServletException {
		super.init(paramServletConfig);
		timerTaskReloadPicklistCache();
		timerTask();

		Context localContext = null;
		try {
			localContext = ContextUtil.getAnonymousContext();
			if (!ContextUtil.isTransactionActive(localContext)) {
				ContextUtil.startTransaction(localContext, false);
			}
			ContextUtil.pushContext(localContext);
			JPO.invoke(localContext, "pgSearchUtils", null, "reloadAllSearchCache", null);
			ContextUtil.popContext(localContext);

			ContextUtil.commitTransaction(localContext);
			return;
		} catch (Exception localException2) {
			ContextUtil.abortTransaction(localContext);
			localException2.printStackTrace();
		} finally {
			if (localContext != null) {
				try {
					localContext.shutdown();
				} catch (Exception localException4) {
					localException4.printStackTrace();
				}
			}
		}

	}

	/**
	 * Code to schedule the Reload Cache process to run daily at a specified time
	 */
	public void timerTask() {
		System.out.println("\n SETTING SEARCH-CACHE-RELOAD as a TIMER TASK ...");
		// Using calendar to fix the time of execution
		Calendar calendar = Calendar.getInstance();

		// Set time of execution. Here, we have to run every day 4:20 PM; so,
		// setting all parameters.
		calendar.set(Calendar.HOUR, nTimeToStartHOUR);
		calendar.set(Calendar.MINUTE, nTimeToStartMIN);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.AM_PM, nTimeToStartAMPM);

		Long currentTime = new Date().getTime();

		// Check if current time is greater than the time to start this Task.
		// If current time is greater, set the time to start on next day.
		if (calendar.getTime().getTime() < currentTime) {
			calendar.add(Calendar.DATE, 1);
		}

		// Calculate the initial delay in milliseconds.
		long lInitialDeleay = calendar.getTime().getTime() - currentTime;
		System.out.println("\t Scheduled Task Time = " + calendar.getTime());

		// The period after which the task should restart is 24 hours.
		long period = 24 * 60 * 60 * 1000;

		// Creating the task
		Runnable task = new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Context localContext = null;
				try {

					localContext = ContextUtil.getAnonymousContext();
					if (!ContextUtil.isTransactionActive(localContext)) {
						ContextUtil.startTransaction(localContext, false);
					}
					ContextUtil.pushContext(localContext);
					final boolean bCacheExpiryCheckNotRequired = true;
					JPO.invoke(localContext, "pgSearchUtils", null, "reloadAllSearchCache",
							new String[] { Boolean.toString(bCacheExpiryCheckNotRequired) });
					ContextUtil.popContext(localContext);

					ContextUtil.commitTransaction(localContext);
					return;

				} catch (Throwable t) {
					System.out.println("Error in executing the task through search cache timer servlet");

					ContextUtil.abortTransaction(localContext);
					t.printStackTrace();

				} finally {
					if (localContext != null) {
						try {
							localContext.shutdown();
						} catch (Exception localException4) {
						}
					}
				}
			}
		};
		// Get an instance of scheduler
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		// execute scheduler at fixed time.
		scheduler.scheduleAtFixedRate(task, lInitialDeleay, period, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * Code to schedule the Reload Cache process to run for every 5 mins for picklist cache
	 */
	public void timerTaskReloadPicklistCache() {
		logger.log(Level.INFO, "\n SETTING PICKLIST-CACHE-RELOAD as a TIMER TASK  ...");
		long lInitialDelayInMS = 0; // Start immediately
		// Creating the task
		@SuppressWarnings("deprecation")
		Runnable task = () -> {
			Context localContext = null;
			try {
				localContext = ContextUtil.getAnonymousContext();
				if (!ContextUtil.isTransactionActive(localContext)) {
					ContextUtil.startTransaction(localContext, false);
				}
				JPO.invoke(localContext, "pgReloadPicklistCache", null, "reloadAllCacheFromPage", null);
				ContextUtil.commitTransaction(localContext);
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Error in executing the timer-task through picklist cache servlet", ex);
				ContextUtil.abortTransaction(localContext);
			} finally {
				if (localContext != null) {
					try {
						localContext.shutdown();
					} catch (Exception localException4) {
					}
				}
			}
		};
		// Get an instance of scheduler
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		// execute scheduler at for every 5 minutes.
		scheduler.scheduleWithFixedDelay(task, lInitialDelayInMS, lPicklistCacheReloadPeriodInMS, TimeUnit.MILLISECONDS);
	}
}
