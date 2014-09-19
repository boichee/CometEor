package com.cometEor.app;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;

//	TO DO: add canonical class elements

public class Auction {
	private User lastBidder = null;
	private int id;
	private int endingTimePeriod;	//in seconds, the final stage of the auction
	private int endTime;			//in seconds, the Unix timestamp of when the auction ends
	// ttl, desc,...
//1287950000 = 10/24 11am

	public Auction( int i ) throws DatabaseException {
		id = i;

		//	 TO DO: get from settings table in DB
//		endingTimePeriod = 60 * 3;
		endingTimePeriod = 60 * 60;

		endTime = DrupalBridge.getAuctionTime( getID() );
	}

	public int getFinalStageTimeIncrement() {
		//	 TO DO: get from settings table in DB
		return 15;	//	15 seconds
	}

	public int processBid( User user, Auction auction, int maxBid, Map<String, Object> output ) {
		int retVal = ResultCode.GENERAL_ERROR;

		if ( user.getBids() < 1 ) {
			return ResultCode.NOT_ENOUGH_BIDS_AVAILABLE;
		}

		int timeIncrement = 0;

		if ( isFinalStage() ) {
			timeIncrement = getFinalStageTimeIncrement();
		}

		try {
			retVal = DrupalBridge.decrementUserBids( user, auction, 1, maxBid, timeIncrement );
			if ( timeIncrement > 0 ) {
				refreshEndTime();
			}
		}
		catch ( Exception e ) {
			System.out.println( "auction " + e.toString() + e.getStackTrace() );
			return ResultCode.EXCEPTION;
		}

		if ( retVal == ResultCode.OK ) {
			try {
				DrupalBridge.insertActionLog( user.getID(), 1, auction.getID(),
												AuctionActionType.PROCESSED_BID, "", "" );
			}
			catch ( Exception e ) {
			}

			lastBidder = user;
		}

		return ResultCode.OK;
	}

	public User getLastBidder() {
		return lastBidder;
	}

	public int getCurrentPrice() throws DatabaseException {
		return DrupalBridge.getAuctionPrice( getID() );
	}

		//	 TO DO: save to DB
	public void setCurrentPrice( int b ) {}

	public int getPriceAdditionAmount() {
		if ( isFinalStage() ) {
			return 1;
		}
		else {
			return 1;
		}
	}

	public int getEndTime() {
		return endTime;
	}

	public void refreshEndTime() throws DatabaseException {
		endTime = DrupalBridge.getAuctionTime( getID() );
	}

	public int getID() { return id; }
	public void setID( int i ) { id = i; }

	public boolean isFinalStage() {
		long endTimeMillis = 1000L * (long) endTime;
		long endingTimePeriodMillis = 1000L * (long) endingTimePeriod;
		long nowTimeMillis = (new Date()).getTime();

		return ( endTimeMillis - nowTimeMillis ) < endingTimePeriodMillis;
	}

	public String toString() {
		return "id=" + getID();
	}
}


/*
	public Date getEndDate() { return endDate; }

	public int getEndingTimePeriod() { return endingTimePeriod; }
	public void setEndingTimePeriod( int etp ) { endingTimePeriod = etp; }

*/


/*
		System.out.println( "processBid1, user=" + user + ", bid=" + bid );

		int currentUserBids = -100;

		if ( user != null && user.getID() > 0 ) {
		}



		synchronized( databaseLock ) {
			try {
				User user = db.getUser( userID );
				if ( user != null && user.getBids() > 0 ) {
					if ( bid <= currentBid.getBid() ) {
						db.adjustUserBids( userID, -1 );
						currentBid.addDelta( 1 );
					}
				}
			}
			catch ( DatabaseException e ) {
			}
		}

		System.out.println( "processBid2, bid=" + bid + " from " + userID +
								": currentBid=" + currentBid.getBid() + ", db=" + db.toString() );

								
								
								
								
								
								

		String s;

		if ( user == null ) {
			s = "no user, bid=" + bid + ", currentUserBids=" + currentUserBids;
		}
		else {
			s = "user=" + user + ", bid=" + bid + ", currentUserBids=" + currentUserBids;
		}

        output.put( "result", s );

		
		
		
		
		
		
		
		
		
		
		
		
		
	public boolean incrementEndTime( int millis ) throws DatabaseException {
		return DrupalBridge.incrementAuctionEndTime( getID(), millis );
	}

		*/
