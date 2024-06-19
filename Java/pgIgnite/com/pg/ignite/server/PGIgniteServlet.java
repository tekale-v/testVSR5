
package com.pg.ignite.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.matrixone.apps.domain.util.ContextUtil;
import com.matrixone.apps.domain.util.FrameworkException;

/**
 * PG Dashboard 2018x.6 <br>
 * Servlet to load all picklist related cache for Platform and Chassis section in Create Document use case
 */
public class PGIgniteServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 243295664529231699L;
	private static final Logger logger = LoggerFactory.getLogger(PGIgniteServlet.class.getName());

	@Override
	public void init(ServletConfig paramServletConfig) throws ServletException {
		logger.info("ENTRY");
		super.init(paramServletConfig);
		try {
			startIgnite();
		} catch (Exception e) {
			logger.error("Error while starting Ignite Server Node :-", e);
			e.printStackTrace();
		}
		logger.info("EXIT");
	}

	public void startIgnite() throws FrameworkException {
		logger.info("ENTRY");
		ContextUtil.runInAnonymousContext(context -> {
			ContextUtil.pushContext(context);
			Object obj = PGIgniteServer.startIgniteServerNode(context);
			ContextUtil.popContext(context);
			return obj;
		});
		logger.info("EXIT");
	}
}