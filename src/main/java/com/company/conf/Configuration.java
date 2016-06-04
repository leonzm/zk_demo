package com.company.conf;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;

import com.company.service.Tool_Zookeeper;
import com.google.common.base.Strings;

public class Configuration {
	
	private static final Logger logger = Logger.getLogger(Configuration.class);
	
	public static final String ip = getLocalIp();

	/**
	 * 获取本地IP
	 * @return
	 */
	private static String getLocalIp() {
        //查询当前操作系统
        Properties p = System.getProperties();
        String osName = p.getProperty("os.name");

        try {
            //如果是windows
            if (osName.matches("(?i).*win.*")) {
                return InetAddress.getLocalHost().getHostAddress().toString();//获得本机IP
            } else {

                //根据"systeminfo"命令查询开机时间
                Process process = Runtime.getRuntime().exec("ifconfig");
                InputStream inputStream = process.getInputStream();
                String s = "";
                byte[] b = new byte[1024];
                while (inputStream.read(b) != -1) {
                    s = s + new String(b);
                }
                if (!Strings.isNullOrEmpty(s)) {
                    Matcher m = Pattern.compile("inet addr:(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})").matcher(s);
                    if (m.find()) {
                        process.destroy();
                        return m.group(1);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("获取ip地址异常", e);
        }

        return "127.0.0.1";
    }
	
	public static void init(String[] args) {
		logger.info("#本地IP：" + ip);
		// 启动集群中的zk
		String myid = null;
		try {
			for (String str : args) {
				if (!Strings.isNullOrEmpty(str) && str.matches("myid=\\d{1}")) {
					myid = str.replace("myid=", "");
				}
			}
			logger.info("#启动zookeeper，myid: " + myid);
			Tool_Zookeeper.startClusterZookeeper(myid);
		} catch (IOException | ConfigException e) {
			logger.error("#项目初始化异常，退出！", e);
			System.exit(-1);
		}
	}
	
}
