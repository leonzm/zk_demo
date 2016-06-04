package com.company.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;

import com.company.conf.Configuration;
import com.google.common.base.Strings;

public class Tool_Zookeeper {

	private static final Logger logger = Logger.getLogger(Tool_Zookeeper.class);
	
	private static Properties zoo_properties = new Properties();
	static {
		try(InputStream zooInput = Tool_Zookeeper.class.getResourceAsStream("/conf/zoo.cfg");) {
			zoo_properties.load(zooInput);
		} catch (IOException e) {}
		logger.info("#读取zoo.cfg");
	}
	
	/**
	 * 启动集群机器中的zookeeper
	 * @param myid 指定的myid，为空时，根据IP判断myid
	 * @throws IOException IOException
	 * @throws ConfigException ConfigException
	 */
	public static void startClusterZookeeper(String myid) throws IOException, ConfigException {
		if (!Strings.isNullOrEmpty(myid) && myid.matches("\\d{1}")) { // 使用指定的myid
			FileUtils.writeStringToFile(new File(zoo_properties.get("dataDir").toString() +  "/myid"), myid);
		} else { // 根据ip找出myid
			for (String key : zoo_properties.stringPropertyNames()) {
				if (key.matches("server\\.\\d{1}")) { // server.1=127.0.0.1:2888:3888
					myid = key.replace("server.", "");
					if (zoo_properties.get(key).toString().split(":")[0].equals(Configuration.ip)) {
						FileUtils.writeStringToFile(new File(zoo_properties.get("dataDir").toString() +  "/myid"), myid);
						break;
					}
				}
			}
		}
		QuorumPeerConfig quorumConfig = new QuorumPeerConfig();
		quorumConfig.parseProperties(zoo_properties);
		QuorumPeerMain peer = new QuorumPeerMain();
		peer.runFromConfig(quorumConfig); // To start the replicated server
	}

	public static void main(String[] args) throws Exception {
		startClusterZookeeper("1");
	}
	
}
