package com.company.testStandalone;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

public class StandaloneZKServer {
	
	private static final Logger logger = Logger.getLogger(StandaloneZKServer.class);
	
	/**
	 * 启动单例zk server
	 * @param tickTime Zookeeper中最小时间单元的长度
	 * @param dataDir Zookeeper服务器存储快照文件的目录
	 * @param clientPort 当前服务器对外的服务端口
	 * @param initLimit Leader服务器等待Follower启动，并完成数据同步的时间
	 * @param syncLimit Leader服务器和Follower之间进行心跳检测的最大延时时间
	 */
	public static void startStandaloneServer1(String tickTime, String dataDir, String clientPort, String initLimit, String syncLimit) {
		Properties props = new Properties();
		props.setProperty("tickTime", tickTime);
		props.setProperty("dataDir", dataDir);
		props.setProperty("clientPort", clientPort);
		props.setProperty("initLimit", initLimit);
		props.setProperty("syncLimit", syncLimit);

		QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
		try {
			quorumConfig.parseProperties(props);
			final ZooKeeperServerMain zkServer = new ZooKeeperServerMain();
			final ServerConfig config = new ServerConfig();
			config.readFrom(quorumConfig);
			zkServer.runFromConfig(config);
		} catch (Exception e) {
			logger.error("Start standalone server faile", e);
		}
	}
	
	/**
	 * 启动单例zk server
	 * @param tickTime Zookeeper中最小时间单元的长度
	 * @param dataDir  Zookeeper服务器存储快照文件的目录
	 * @param clientPort 当前服务器对外的服务端口
	 * @param maxcnxn 客户端最大连接数，通过 IP 来区分不同的客户端
	 */
	public static void startStandaloneServer2(String tickTime, String dataDir, String clientPort, String maxcnxn) {
		ZooKeeperServerMain.main(new String[]{clientPort, dataDir, tickTime, maxcnxn}); // port datadir [ticktime] [maxcnxns]"
	}

	public static void main(String[] args) throws Exception {  
		startStandaloneServer1("2000", new File(System.getProperty("java.io.tmpdir"), "zookeeper").getAbsolutePath(), "2181", "10", "5");
		//startStandaloneServer2("2000", new File(System.getProperty("java.io.tmpdir"), "zookeeper").getAbsolutePath(), "2181", "44");
    }
	
}
