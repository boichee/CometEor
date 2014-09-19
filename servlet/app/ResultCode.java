package com.cometEor.app;

class ResultCode {
	public static final int OK = 1;
	public static final int USER_NOT_FOUND = 2;
	public static final int BID_PRICE_NOT_HIGH_ENOUGH = 3;
	public static final int NOT_ENOUGH_BIDS_AVAILABLE = 4;
	public static final int EXCEPTION = 5;
	public static final int GENERAL_ERROR = 6;
	
	public static String getMessage( int code ) {
		if ( code == OK ) {
			return "OK";
		}
		else if ( code == USER_NOT_FOUND ) {
			return "USER_NOT_FOUND";
		}
		else if ( code == BID_PRICE_NOT_HIGH_ENOUGH ) {
			return "BID_PRICE_NOT_HIGH_ENOUGH";
		}
		else if ( code == NOT_ENOUGH_BIDS_AVAILABLE ) {
			return "NOT_ENOUGH_BIDS_AVAILABLE";
		}
		else if ( code == EXCEPTION ) {
			return "EXCEPTION";
		}
		else if ( code == GENERAL_ERROR ) {
			return "GENERAL_ERROR";
		}
		else {
			return "UNKNOWN";
		}
	}
}
