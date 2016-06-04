package com.company.launcher;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

import com.company.conf.Configuration;

public class Launcher {
	
	private static Logger logger = Logger.getLogger(Launcher.class);
	
	private static final int PORT = 8080;
	private static final String WEBAPP = "src/main/webapp";
	private static final String CONTEXTPATH = "/";
	private static final String DESCRIPTOR = "src/main/webapp/WEB-INF/web.xml";

	/*
	 * 创建 Jetty Server，指定其端口、web目录、根目录、web路径
	 * @param port
	 * @param webApp
	 * @param contextPath
	 * @param descriptor
	 * @return Server
	 */
	public static Server createServer(int port, String webApp, String contextPath, String descriptor) {
		Server server = new Server();
		//设置在JVM退出时关闭Jetty的钩子
		//这样就可以在整个功能测试时启动一次Jetty,然后让它在JVM退出时自动关闭
		server.setStopAtShutdown(true);
		
		ServerConnector connector = new ServerConnector(server); 
		connector.setPort(port); 
		//解决Windows下重复启动Jetty不报告端口冲突的问题
		//在Windows下有个Windows + Sun的connector实现的问题,reuseAddress=true时重复启动同一个端口的Jetty不会报错
		//所以必须设为false,代价是若上次退出不干净(比如有TIME_WAIT),会导致新的Jetty不能启动,但权衡之下还是应该设为False
		connector.setReuseAddress(false);
		server.setConnectors(new Connector[]{connector});
		
		WebAppContext webContext = new WebAppContext(webApp, contextPath);
		webContext.setDescriptor(descriptor);
		// 设置webapp的位置
		webContext.setResourceBase(webApp);
		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
				
		server.setHandler(webContext);
		
		return server;
	}
	
	/**
	 * 启动jetty服务
	 * 
	 */
	public void startJetty() {
		final Server server = Launcher.createServer(PORT, WEBAPP, CONTEXTPATH, DESCRIPTOR);
		
		try {
			//server.stop();
			server.start();
			server.join();
			
		} catch (Exception e) {
			logger.warn("启动 jetty server 失败", e);
			System.exit(-1);
		}
	}
	
	public static void main(String[] args) {
		Configuration.init(args);
		new Launcher().startJetty();
		// jetty 启动后的测试url
		// http://localhost:8080/hello/hello
	}
	
}