package com.pg.dsm.sapview.cron;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pg.dsm.sapview.config.SAPConfig;
import com.pg.dsm.sapview.services.BOMeDeliveryController;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 */
public class BOMeDeliveryJobClient {
	private static final Logger logger = Logger.getLogger(BOMeDeliveryJobClient.class.getName());

	public static void main(String[] args) {

		Instant startTime = Instant.now();
		logger.log(Level.INFO, "Starting Cron execution...");

		SAPConfig config = new SAPConfig().config();

		BOMeDeliveryController controller = new BOMeDeliveryController(config);
		controller.execute();

		Instant endTime = Instant.now();
		Duration duration = Duration.between(startTime, endTime);
		logger.log(Level.INFO, "SAP BOM eDelivery Job Execution - took|{0} ms|{1} sec| {2} min",
				new String[] { duration.toMillis() + "", duration.getSeconds() + "", duration.toMinutes() + "" });
		System.exit(0);
	}
}
