package com.win4causes.app;

class AuctionActionType {
	public static final int PROCESSED_BID = 1;
	
	public static String getMessage( int code ) {
		if ( code == PROCESSED_BID ) {
			return "PROCESSED_BID";
		}
		else {
			return "UNKNOWN";
		}
	}
}
