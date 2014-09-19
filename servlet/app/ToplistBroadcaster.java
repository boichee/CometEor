package com.cometEor.app;

import java.util.Map;
import java.util.HashMap;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.server.AbstractService;

public class ToplistBroadcaster implements Runnable {
	private BayeuxServer bayeux;
	private Auction auction = null;
	private int bid = 0;

	public ToplistBroadcaster( BayeuxServer bx ) {
		this.bayeux = bx;
	}

	public void setAuction( Auction a ) {
		auction = a;
	}

	public Auction getAuction() {
		return auction;
	}

	public void run() {
		Object[] toplist;

		while ( true ) {
			try {
				Thread.sleep( 30000 );
			}
			catch ( InterruptedException ie ) {
			}

			try {
				toplist = DrupalBridge.getToplist( auction.getID() );
			}
			catch ( Exception e ) {
				System.out.println( "can't get toplist" );
				continue;
			}

			//ServerChannel channel = bayeux.getChannel( "/auction/bidprice", true );	//	beta1
			ServerChannel channel = bayeux.getChannel( "/auction/toplist" );
			System.out.println( "Pushing " + toplist + " to " + channel );
			if ( channel != null ) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put( "toplist", toplist );

				//System.out.println( "Pushing " + data.toString() );
				//channel.publish( bayeux.getSession(), data, null );
				channel.publish( null, data, null );
			}
		}
	}
}


/*
	public void addDelta( int d ) {
		bid = bid + d;
	}

*/
