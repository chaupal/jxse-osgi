package net.jxta.core.server.http;

import java.net.InetAddress;

public interface IServerStarter {

	public abstract void addServletHandler( InetAddress useInterface, int port);

	public void start() throws Exception;
	
	public void stop( Object caller );

	void addServlet( Object caller, String callerId, String servletName);

}