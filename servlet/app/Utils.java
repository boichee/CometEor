package com.cometEor.app;

class Utils {
	public static int getInteger( Object obj ) {
		if ( obj == null ) {
			System.out.println( "11" );
			return 0;
		}

		if ( obj instanceof java.lang.Integer ) {
			return ( (java.lang.Integer) obj ).intValue();
		}

		if ( obj instanceof java.lang.Long ) {
			return ( (java.lang.Long) obj ).intValue();
		}

		String s;

		try {
			s = (String) obj;
		}
		catch ( Exception e ) {
			return 0;
		}

		if ( s.length() > 0 ) {
			try {
				return Integer.parseInt( s );
			}
			catch ( Exception e ) {
			}
		}

		return 0;
	}
}
