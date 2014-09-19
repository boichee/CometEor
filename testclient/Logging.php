<?php

//
// (c) 2010 Chris Kelly
// very simple Logging class for PHP
//

class Logging {
	public static function log() {
		$ary = func_get_args();
		if ( !@error_log( implode( ': ', $ary ) . "\n\n", 3, 'myclasseserrorlog.log' ) ) {
			@error_log( implode( ': ', $ary ) . "\n\n", 0 );
		}
	}

	private function __construct() {
	}
}

