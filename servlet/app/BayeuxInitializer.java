package com.win4causes.app;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.cometd.bayeux.server.BayeuxServer;

public class BayeuxInitializer extends GenericServlet {
	@Override
	public void init() throws ServletException {
		BayeuxServer bayeux = (BayeuxServer) getServletContext().getAttribute( BayeuxServer.ATTRIBUTE );

		bayeux.addExtension(new org.cometd.server.ext.TimesyncExtension());

/*
		BayeuxAuthenticator4 authenticator = new BayeuxAuthenticator4();
		bayeux.setSecurityPolicy( authenticator );
		bayeux.addExtension( authenticator );
*/

		try {
			new AuctionService( bayeux );
		}
		catch ( Exception e ) {
			throw new ServletException( "Can't create auction: " + e.getMessage() );
		}
	}

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		throw new ServletException();
	}
}

