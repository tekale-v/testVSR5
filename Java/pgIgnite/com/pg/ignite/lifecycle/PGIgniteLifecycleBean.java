
package com.pg.ignite.lifecycle;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.ignite.Ignite;
import org.apache.ignite.lifecycle.LifecycleBean;
import org.apache.ignite.lifecycle.LifecycleEventType;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;

import matrix.db.JPO;

public class PGIgniteLifecycleBean implements LifecycleBean {
	private static final long lPicklistCacheReloadPeriodInMS = 300000; // 5 minutes
	private static final Logger logger = LoggerFactory.getLogger(PGIgniteLifecycleBean.class.getName());
	@IgniteInstanceResource
	public Ignite ignite;

	@Override
	public void onLifecycleEvent(LifecycleEventType evt) {
		if (evt == LifecycleEventType.AFTER_NODE_START) {
			logger.info("DB After the node (consistentId = {}) starts.\n", ignite.cluster().node().consistentId());
			timerTaskReloadPicklistCache();
		}
		if (evt == LifecycleEventType.AFTER_NODE_STOP) {
			logger.info("DB After the node (consistentId = {}) stops.\n", ignite.cluster().node().consistentId());
		}
	}

	/**
	 * Code to schedule the Reload Cache process to run for every 5 mins for picklist cache
	 */
	public void timerTaskReloadPicklistCache() {
		/*
		 * String webRootPath = this.getServletContext().getRealPath("/"); String [] args = {webRootPath};
		 */
		logger.info("\n SETTING PICKLIST-CACHE-RELOAD as a TIMER TASK  ...");
		long lInitialDelayInMS = 0; // Start immediately
		// Creating the task
		Runnable task = () -> {
			try {
				ContextUtil.runInAnonymousContext(context -> {
					ContextUtil.pushContext(context);
					int nInvokeJPOInvokeStatus = JPO.invoke(context, "pgReloadPicklistCache", null, "reloadAllCacheFromPage", null);
					ContextUtil.popContext(context);
					return nInvokeJPOInvokeStatus;
				});
			} catch (FrameworkException e) {
				logger.error("Error while executing Java timer task to reload picklist cache into Ignite server", e);
			}
		};
		// Get an instance of scheduler
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		// execute scheduler at for every 5 minutes.
		scheduler.scheduleWithFixedDelay(task, lInitialDelayInMS, lPicklistCacheReloadPeriodInMS, TimeUnit.MILLISECONDS);
	}
}