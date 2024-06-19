package com.pg.dsm.sapview.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.matrixone.apps.domain.DomainConstants;
import com.matrixone.apps.domain.util.MqlUtil;
import com.matrixone.apps.domain.util.StringUtil;
import com.pg.dsm.sapview.beans.bo.SAPPartBean;
import com.pg.dsm.sapview.config.SAPConfig;
import com.pg.dsm.sapview.config.SAPConstants;
import com.pg.dsm.sapview.config.SAPCronConfig;
import com.pg.dsm.sapview.utils.MarkUtils;
import com.pg.dsm.sapview.utils.SAPBomUtils;
import com.pg.dsm.sapview.utils.StringHelper;
import com.pg.v3.custom.pgV3Constants;

import matrix.util.StringList;

/**
 * @author DSM(Sogeti) - Added for 2018x.6 Dec CW SAP Requirement #40804,#40805.
 */
public class BOMeDeliveryController {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private SAPConfig config;

	/**
	 * @param conf
	 */
	public BOMeDeliveryController(SAPConfig conf) {
		this.config = conf;
	}

	/**
	 * 
	 */
	public void execute() {
		if (this.config.isLoaded()) {

			SAPCronConfig sapConfig = new SAPCronConfig(this.config).loadConfig();

			if (!sapConfig.isCronActive()) {
				sapConfig.updateCronAttrOnStart();

				this.processingPendingObjects();

				sapConfig.updateCronAttrOnEnd();
			} else {
				logger.log(Level.WARNING, "CTRLM is running or stuck..");
			}
		} else {
			logger.log(Level.WARNING, "Failed to load CTRLM configuration..");
		}
	}

	/**
	 * 
	 */
	private void processingPendingObjects() {

		Date today = Calendar.getInstance().getTime();
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
		String strToday = formatter.format(today);
		SimpleDateFormat sDateFormatinsec = new SimpleDateFormat("yyyyMMdd");
		String sCurDate = sDateFormatinsec.format(today);

		Date startTime = new Date();
		String strOutputFileName = this.config.getProperties().getSapOutputLogsPath() + File.separator
				+ this.config.getProperties().getSapOutputLogsFileName() + sCurDate + ".log";

		File file = new File(strOutputFileName);

		if (!file.exists()) {
			boolean isFileCreated = false;
			try {
				isFileCreated = file.createNewFile();
			} catch (IOException e) {
				logger.log(Level.WARNING, null, e);
			}
			if (isFileCreated)
				logger.log(Level.INFO, "Log file created :{0}", strOutputFileName);
		}
		
		File mqlOutputFile = null;
		
		try (PrintStream out = new PrintStream(new FileOutputStream(strOutputFileName, true))) {

			out.append("\n\n====================== " + strToday + "===========================\n");
			out.append("process for IPS Start " + startTime + "\n");
			out.append("Process done for following IPS  :\n");
			out.append("\t Type \t \t  \t   \t\t  \t  Name \t \t\t  Revision  \t    \t ID  \n");
			String outputFile = this.config.getProperties().getSapInputFilePath();

			String mqlQuery = "temp query bus \"$1\" $2 $3  where \"$4\" select $5 $6 $7 dump $8 output $9";

			/*Its CTRLM job the following query does 2 things in single call 
			 * 1. Querying the database 
			 * 2. Write output to the output file
			 */
			
			MqlUtil.mqlCommand(this.config.getContext(), mqlQuery, this.config.getProperties().getSapFetchQueryTypes(),
					DomainConstants.QUERY_WILDCARD, DomainConstants.QUERY_WILDCARD,
					this.config.getProperties().getSapFetchQueryWhere(), DomainConstants.SELECT_ID,
					SAPConstants.SELECT_ATTRIBUTE_PGBOMEDELIVERY, SAPConstants.SELECT_ATTRIBUTE_PGBOMEDELIVERYPARENT,
					pgV3Constants.DUMP_CHARACTER, outputFile);

			mqlOutputFile = new File(outputFile);

			int count = 0;
			if (mqlOutputFile.exists()) {
				boolean bIsWritable = mqlOutputFile.setWritable(true, false);
				boolean bIsExecutable = mqlOutputFile.setExecutable(true, false);
				boolean bIsReadable = mqlOutputFile.setReadable(true, false);
				logger.log(Level.INFO, "Query output file is created :{0}", mqlOutputFile.getCanonicalPath());
				if (bIsWritable && bIsExecutable && bIsReadable) {

					List<String> linesList = this.getInputFileContentAsList(outputFile);

					Map<Object, Object> objectMap;
					StringList slOutputLine;
					SAPPartBean productPart;
					SAPBomUtils utils = new SAPBomUtils(this.config);
					Date procesDate;
					String logData;
					for (String outputLine : linesList) {
						count++;
						objectMap = new HashMap<>();
						slOutputLine = StringHelper.split(outputLine, pgV3Constants.DUMP_CHARACTER);
						procesDate = new Date();
						out.append(
								slOutputLine.get(0) + "\t \t\t " + slOutputLine.get(1) + "\t\t   " + slOutputLine.get(2)
										+ "  \t  " + slOutputLine.get(3) + " \t " + procesDate + " => Starts\n");
						logger.log(Level.INFO, "Start Processing object:{0}|{1}|{2}",
								new String[] { slOutputLine.get(0), slOutputLine.get(1), slOutputLine.get(2) });
						objectMap.put(DomainConstants.SELECT_TYPE, slOutputLine.get(0));
						objectMap.put(DomainConstants.SELECT_NAME, slOutputLine.get(1));
						objectMap.put(DomainConstants.SELECT_REVISION, slOutputLine.get(2));
						objectMap.put(DomainConstants.SELECT_ID, slOutputLine.get(3));
						objectMap.put(SAPConstants.SELECT_ATTRIBUTE_PGBOMEDELIVERY, slOutputLine.get(4));
						objectMap.put(SAPConstants.SELECT_ATTRIBUTE_PGBOMEDELIVERYPARENT, slOutputLine.get(5));

						productPart = new SAPPartBean(objectMap, this.config);
						if (productPart.isLoaded()) {

							utils.sendValidPartToBomDelivery(productPart);

							if (utils.getDeliveredParts() != null && !utils.getDeliveredParts().isEmpty()) {
								logData = "\t Successfully delivered parts:";
								out.append(
										logData + StringUtil.join(utils.getDeliveredParts(), pgV3Constants.SYMBOL_COMMA)
												+ "\n");
								logger.log(Level.INFO, "{0}", logData + utils.getDeliveredParts().size() + " Parts");
							}

							if (utils.getFailureParts() != null && !utils.getFailureParts().isEmpty()) {
								logData = "\t Failed to delivere parts:";
								out.append(logData
										+ StringUtil.join(utils.getFailureParts(), pgV3Constants.SYMBOL_COMMA) + "\n");
								logger.log(Level.INFO, "{0}", logData + utils.getFailureParts().size() + " Parts");
							}

							// un-marking
							MarkUtils.doPartUnMarking(this.config.getContext(), productPart);
						}
						logger.log(Level.INFO, "End Processing object:{0}|{1}|{2}",
								new String[] { slOutputLine.get(0), slOutputLine.get(1), slOutputLine.get(2) });
						procesDate = new Date();
						out.append(
								slOutputLine.get(0) + "\t \t\t " + slOutputLine.get(1) + "\t\t   " + slOutputLine.get(2)
										+ "  \t  " + slOutputLine.get(3) + " \t " + procesDate + " => Ends\n\n");
					}
				}

			} else {
				logger.log(Level.WARNING, "File doesnt exist : {0}", outputFile);
			}
			Date endTime = new Date();
			out.append("process for IPS End " + endTime + " Part count:" + count + "\n");
		} catch (Exception e) {
			logger.log(Level.WARNING, null, e);
		} finally {
			if (null != mqlOutputFile) {
				boolean bIsFileDelete = mqlOutputFile.delete();
				if (bIsFileDelete)
					logger.log(Level.INFO, "Is File Deleted: {0}", bIsFileDelete);
			}
				
		}
	}

	/**
	 * @param filePath
	 * @return
	 */
	private List<String> getInputFileContentAsList(String filePath) {
		List<String> linesList = new ArrayList<>();
		try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
			lines.forEach(line -> linesList.add(line));
		} catch (IOException e) {
			logger.log(Level.WARNING, null, e);
		}
		return linesList;
	}
}
