<?php

//
// (c) 2010 Chris Kelly
// very simple MySQL class for PHP
//

require_once( 'Logging.php' );
require_once( 'SafeSQL.class.php' );

class Database {
	protected $dbLink;
	protected $safesql;

	function __construct( $db_ip, $db_user, $db_pass, $db_name ) {
		$this->dbLink = mysql_connect( $db_ip, $db_user, $db_pass );

		mysql_select_db( $db_name, $this->dbLink );
		$this->safesql = new SafeSQL_MySQL( $this->dbLink );
	}

	function getObjectArray( $query, $argAry = NULL, $debug = FALSE ) {
		if ( $argAry ) {
			$query = $this->safesql->query( $query, $argAry );
		}

		if ( $debug ) {
			Logging::log( 'db.log', $query );
		}

		$res = mysql_query( $query, $this->dbLink );
		if ( !$res ) {
			Logging::log( 'db.log', $query, mysql_error( $this->dbLink ) );
			return NULL;
		}

		$retVal = array();

		while ( $obj = mysql_fetch_object( $res ) ) {
			$retVal[] = $obj;
		}

		return $retVal;
	}

	function getObject( $query, $argAry = NULL, $debug = FALSE ) {
		if ( $argAry ) {
			$query = $this->safesql->query( $query, $argAry );
		}

		if ( $debug ) {
			Logging::log( 'db.log', $query );
		}

		$res = mysql_query( $query, $this->dbLink );
		if ( !$res ) {
			Logging::log( 'db.log', $query, mysql_error( $this->dbLink ) );
			return NULL;
		}

		return mysql_fetch_object( $res );
	}

	function query( $query, $argAry = NULL, $debug = FALSE ) {
		if ( $argAry ) {
			$query = $this->safesql->query( $query, $argAry );
		}

		if ( $debug ) {
			Logging::log( 'db.log', $query );
		}

		$result = mysql_query( $query, $this->dbLink );

		if ( !$result ) {
			Logging::log( 'db.log', $query, mysql_error( $this->dbLink ) );
			return NULL;
		}

		return $result;
	}

	function getDBLink() {
		return $this->dbLink;
	}

	function lastInsertID() {
		return mysql_insert_id( $this->dbLink );
	}

	function getSafeSQL() {
		return $this->safesql;
	}

	function insert( $query, $argAry = NULL, $debug = FALSE ) {
		if ( $argAry ) {
			$query = $this->safesql->query( $query, $argAry );
		}

		if ( $debug ) {
			Logging::log( 'db.log', $query );
		}

		$res = @mysql_query( $query, $this->dbLink );
		if ( $res && @mysql_affected_rows( $this->dbLink ) > 0 ) {
			return TRUE;
		}

		Logging::log( 'db.log', $query, mysql_error( $this->dbLink ) );

		return FALSE;
	}
}

