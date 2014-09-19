package com.win4causes.app;

import java.util.Map;
import java.util.HashMap;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.server.AbstractService;

public class AuctionPriceBroadcaster implements Runnable {
	private BayeuxServer bayeux;
	private Auction auction = null;
	private int bid = 0;

    public AuctionPriceBroadcaster( BayeuxServer bx ) {
		this.bayeux = bx;
    }

	public void setAuction( Auction a ) {
		auction = a;
	}

	public Auction getAuction() {
		return auction;
	}

	public void run() {
		while ( true ) {
			try {
				Thread.sleep( 2000 );
			}
			catch ( InterruptedException ie ) {
			}

			User user;
			int price, endTime;

			try {
				price = auction.getCurrentPrice();
				user = auction.getLastBidder();
				endTime = auction.getEndTime();
			}
			catch ( Exception e ) {
				continue;
			}

			//ServerChannel channel = bayeux.getChannel( "/auction/bidprice", true );	//	beta1
			ServerChannel channel = bayeux.getChannel( "/auction/bidprice" );
			//System.out.println( "Pushing " + price + " to " + channel );
			if ( channel != null ) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put( "price", price );
				data.put( "endTime", endTime );
				if ( user != null ) {
					data.put( "userName", user.getName() );
					data.put( "userLink", user.getLink() );
				}
				else {
					data.put( "userName", "" );
					data.put( "userLink", "" );
				}

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
