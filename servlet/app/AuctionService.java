package com.cometEor.app;

import java.sql.*;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;

import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.AbstractService;

public class AuctionService extends AbstractService {
	private Auction auction;
	private AuctionPriceBroadcaster auctionPriceBroadcaster;
	private ToplistBroadcaster toplistBroadcaster;
	private Object databaseLock = new Object();
	private ConcurrentMap<Integer, String> _members = new ConcurrentHashMap<Integer, String>();

    public AuctionService( BayeuxServer bayeux ) throws DatabaseException {
        super( bayeux, "auction" );

		auction = DrupalBridge.getCurrentAuction();

		auctionPriceBroadcaster = new AuctionPriceBroadcaster( getBayeux() );
		auctionPriceBroadcaster.setAuction( auction );

		toplistBroadcaster = new ToplistBroadcaster( getBayeux() );
		toplistBroadcaster.setAuction( auction );

        addService( "/service/auction", "processBid" );
        //addService( "/service/testing_createuser", "testing_createUser" );

		( new Thread( auctionPriceBroadcaster ) ).start();
		( new Thread( toplistBroadcaster ) ).start();
    }

    public Map<String, Object> processBid( ServerSession remote, Message message ) {
		int resultCode = -1;

		if ( remote == null ) {
			System.out.println( "processBid: remote is null" );
			return null;
		}

        Map<String, Object> output = new HashMap<String, Object>();

		try {
			Map<String, Object> input = message.getDataAsMap();
			String cookie = (String) input.get( "cookie" );
			//int userID = Utils.getInteger( input.get( "userID" ) );
			int bid = Utils.getInteger( input.get( "bid" ) );

			User user = DrupalBridge.createUserFromCookie( cookie );

			if ( user == null ) {
				resultCode = ResultCode.USER_NOT_FOUND;
			}
			else {
				_members.putIfAbsent( new Integer( user.getID() ), remote.getId() );
				System.out.println( "user added: " + _members.toString() );

				resultCode = auction.processBid( user, auction, bid, output );
				if ( resultCode == ResultCode.OK ) {
					output.put( "userbids", user.getBids() );
				}
			}
		}
		catch ( Exception e ) {
			output.put( "originalResultCode", new Integer( resultCode ) );
			output.put( "exception", e.toString() );
			output.put( "trace", e.getStackTrace().toString() );
			resultCode = ResultCode.EXCEPTION;
		}

		output.put( "resultCode", new Integer( resultCode ) );
		output.put( "resultMessage", ResultCode.getMessage( resultCode ) );

		System.out.println( "processBid3, returning " + output.toString() );

		return output;

        //remote.deliver( getServerSession(), "/service/auction", output, null );
    }


}


/*
    public Map<String, Object> testing_createUser( ServerSession remote, Message message ) {
		if ( remote == null ) {
			System.out.println( "testing_createUser: remote is null" );
			return null;
		}

        Map<String, Object> output = new HashMap<String, Object>();

		int numBids = 20 + ( new Random() ).nextInt( 200 );
		int newUserID = 0;

		synchronized( databaseLock ) {
			try {
				newUserID = db.getNextUserID();
				db.createUser( newUserID, numBids );
			}
			catch ( DatabaseException e ) {
			}
		}

		output.put( "result", newUserID );

		System.out.println( "testing_createUser, db=" + db.toString() );

		return output;
        //remote.deliver( getServerSession(), "/service/testing_createuser", output, null );
    }

	
	
	
	
	
	
	
	
		private IDatabase db;
		db = new DatabaseSimple();

*/


