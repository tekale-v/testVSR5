
package com.pg.ignite.server;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.IgniteEvents;
import org.apache.ignite.Ignition;
//import org.apache.ignite.cluster.ClusterState; // DB: Available in 2.12.0. Not available in 22x Ignite version=2.7.5
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.PartitionLossPolicy;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.CacheRebalancingEvent;
import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.events.Event;
import org.apache.ignite.events.EventType;
import org.apache.ignite.internal.IgnitionEx;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pg.ignite.ipfinder.PGIgniteIPFinder;
import com.pg.ignite.lifecycle.PGIgniteLifecycleBean;

import matrix.db.Context;
import matrix.util.MatrixException;

public class PGIgniteServer {
	private PGIgniteServer() {
		// Do nothing
	}

	private static final String USER_DIRECTORY = "user.dir";
	private static final String TEMP_FOLDER_NAME = "temp";
	// Current Stress A envs. :- azl-enah201.np-cloud-pg.com (137.182.253.14) AND azl-enah202.np-cloud-pg.com (137.182.253.15)
	// static String STR_IP_ADDRESS_PORT = "137.182.253.14:47500..47509,137.182.253.15:47500..47509";
	private static final String CLIENT_CACHE_NAME = "PICKLIST";
	private static final String IGNITE_FOLDER_NAME = "ignite";
	private static final String IGNITE_CONFIG_XML = "pg-ignite-config.xml";
	private static final Logger logger = LoggerFactory.getLogger(PGIgniteServer.class.getName());

	public static Ignite startIgniteServerNode(Context context) throws Exception {
		logger.info("ENTRY");
		Ignite ignite = null;
		// Class.forName("org.apache.ignite.internal.util.spring.IgniteSpringHelperImpl");
		try {
			ignite = Ignition.getOrStart(getIgniteConfig(context));
			ignite.cluster().state(ClusterState.ACTIVE); // DB: Works with 2.11.0 = 3dexp 2022x FD02
			// Added the following 2 LOCs to correct the following exception :
			// org.apache.ignite.client.ClientException: Ignite failed to process request [3]: class
			// org.apache.ignite.internal.processors.cache.CacheInvalidStateException: Failed to execute the cache operation (all partition owners
			// have left the grid, partition data has been lost) [cacheName=PICKLIST, partition=503, key=UserKeyCacheObjectImpl [part=503,
			// val=pgCache_pgPLIDataMerge, hasValBytes=false]] (server status code [1])
			ignite.cluster().baselineAutoAdjustEnabled(true);
			ignite.cluster().baselineAutoAdjustTimeout(30000);
			// ignite.cluster().active(true); // DB : Works with 2.7.5 = 3dexp 2022x FD02
			Collection<String> collIgniteNodeIpAddr = ignite.configuration().getDiscoverySpi().getLocalNode().addresses();
			Integer intIgniteNodePort = ignite.configuration().getDiscoverySpi().getLocalNode().attribute("TcpCommunicationSpi.comm.tcp.port");
			logger.info("local ignite server node communication ip address : {}", collIgniteNodeIpAddr);
			logger.info("local ignite server node communication port : {}", intIgniteNodePort);
			PGIgniteIPFinder.addIgniteNodeAddrsInDatabase(context, collIgniteNodeIpAddr);
			IgniteEvents events = ignite.events();
			// Local listener that listens to local events.
			IgnitePredicate<DiscoveryEvent> localListener = evt -> {
				logger.info("Received event [evt = {}, message = {}, type = {}", evt.name(), evt.message(), evt.type());
				return true; // Continue listening.
			};
			// Subscribe to the Discovery events that are triggered on the local node.
			events.localListen(localListener, EventType.EVT_NODE_JOINED, EventType.EVT_NODE_LEFT);
			
			
			IgnitePredicate<Event> locLsnrPartitionLoss = evt -> {
				logger.info("Received event for Partition Loss [evt = {}, message = {}, type = {}", evt.name(), evt.message(), evt.type());
			    CacheRebalancingEvent cacheEvt = (CacheRebalancingEvent) evt;
			    int lostPart = cacheEvt.partition();
			    //ClusterNode node = cacheEvt.discoveryNode();
			    logger.info("Lost partiotion number is {}", String.valueOf(lostPart));
			    return true; // Continue listening.
			};
			events.localListen(locLsnrPartitionLoss, EventType.EVT_CACHE_REBALANCE_PART_DATA_LOST);
		} catch (Exception e) {
			logger.error("Exception in method startIgniteServerNode():", e);
			throw e;
		}
		logger.info("EXIT : PGIgniteServer : startIgniteServerNode");
		return ignite;
	}

	public static IgniteConfiguration getIgniteConfig(Context context) throws IgniteSpiException, MatrixException {
		logger.info("ENTRY : PGIgniteServer : getIgniteConfig");
		try {
			return getIgniteConfigFromXMLFile();
		} catch (IgniteCheckedException ice) {
			logger.error("Exception while retrieving ignite config from XML file: {}", ice.getMessage());
			logger.info("Ignite configuration will be set with default values coded in this class");
			return setIgniteConfig(context);
		}
	}

	private static IgniteConfiguration setIgniteConfig(Context context) throws IgniteSpiException, MatrixException {
		logger.info("ENTRY");
		String strCurrentUserDir = System.getProperty(USER_DIRECTORY);
		String strTempFolderPath = strCurrentUserDir + File.separator + ".." + File.separator + TEMP_FOLDER_NAME;
		IgniteConfiguration cfg = new IgniteConfiguration();
		// Register the implementation in the node configuration.
		cfg.setLifecycleBeans(new PGIgniteLifecycleBean());
		cfg.setIncludeEventTypes(EventType.EVT_NODE_JOINED, EventType.EVT_NODE_LEFT, EventType.EVT_NODE_FAILED);
		// Override default discovery SPI.
		cfg.setGridLogger(new Slf4jLogger()); //
		cfg.setDiscoverySpi(getTcpDiscoverySpiWithAddrsFromDatabase(context));
		// cfg.setDiscoverySpi(getTcpDiscoveryJdbcIpFinder());
		// cfg.setPeerClassLoadingEnabled(true); //if enabled, will automatically redeploy Java or Scala code on every node in a grid each time the
		// code changes.
		cfg.setCacheConfiguration(getCacheCfg());
		String strIgniteWorkDir = new StringBuilder(strTempFolderPath).append(File.separator).append(IGNITE_FOLDER_NAME).toString();
		logger.info("Ignite Work Directory = {}", strIgniteWorkDir);
		cfg.setWorkDirectory(strIgniteWorkDir);
		// Data storage configuration
		cfg.setDataStorageConfiguration(getstorageCfg());
		cfg.setFailureDetectionTimeout(2_000);
		cfg.setClientFailureDetectionTimeout(5_000);
		logger.info("EXIT");
		return cfg;
	}

	private static DataStorageConfiguration getstorageCfg() {
		DataStorageConfiguration storageCfg = new DataStorageConfiguration();
		storageCfg.getDefaultDataRegionConfiguration().setPersistenceEnabled(true);
		// storageCfg.setStoragePath(strTempFolderPath); // this is not needed. By default storage directory will be the ignite workdirectory as set
		// above
		return storageCfg;
	}

	private static CacheConfiguration<String, String> getCacheCfg() {
		CacheConfiguration<String, String> cacheCfg = new CacheConfiguration<>(CLIENT_CACHE_NAME);
		cacheCfg.setRebalanceMode(CacheRebalanceMode.SYNC); // default is ASYNC
		cacheCfg.setCacheMode(CacheMode.REPLICATED);
		cacheCfg.setPartitionLossPolicy(PartitionLossPolicy.READ_WRITE_SAFE); // default is IGNORE
		cacheCfg.setBackups(1);
		cacheCfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC); // default is PRIMARY_SYNC
		return cacheCfg;
	}

	private static IgniteConfiguration getIgniteConfigFromXMLFile() throws IgniteCheckedException {
		logger.info("ENTRY : PGIgniteServer : getIgniteConfigFromXMLFile()");
		return IgnitionEx.loadConfiguration(IGNITE_CONFIG_XML).get1();
	}

	private static TcpDiscoverySpi getTcpDiscoverySpiWithAddrsFromDatabase(Context context) throws IgniteSpiException, MatrixException {
		TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
		TcpDiscoveryVmIpFinder tcpDiscoveryVmIpFinder = new TcpDiscoveryVmIpFinder();
		tcpDiscoveryVmIpFinder.setAddresses(PGIgniteIPFinder.getCurrentIgniteNodeAddrsFromDatabase(context));
		tcpDiscoverySpi.setIpFinder(tcpDiscoveryVmIpFinder);
		return tcpDiscoverySpi;
	}

	@SuppressWarnings("unused")
	private static TcpDiscoverySpi getTcpDiscoverySpi() {
		// Multicast and Static IP Finder
		TcpDiscoverySpi spiMultiAndStatic = new TcpDiscoverySpi();
		TcpDiscoveryMulticastIpFinder ipFinderMultiAndStatic = new TcpDiscoveryMulticastIpFinder();
		// Set initial IP addresses.
		// Note that you can optionally specify a port or a port range.
		ipFinderMultiAndStatic.setAddresses(Arrays.asList(""));
		// ipFinderMultiAndStatic.setAddresses("10.201.69.232:47500","10.201.69.232:47501", "10.32.10.168:47500");
		spiMultiAndStatic.setIpFinder(ipFinderMultiAndStatic);
		return spiMultiAndStatic;
	}
}