<?php

require_once( 'config.php' );
require_once( 'Database.php' );
require_once( 'CommunicationManager.php' );
require_once( 'CommandUserBid.php' );
require_once( 'CommandUserInfo.php' );

$db = new Database( DB_HOST, DB_USER, DB_PWD, DB_NAME );

$ary = $db->getObjectArray( "SELECT uid FROM user" );

foreach ( $ary as $obj ) {
	$comm = new CommunicationManager( REMOTE_HOST, REMOTE_PORT );
	$comm->connect();
	$bidAmount = rand( 1, 3 );
	$cmd = new CommandUserBid( $obj->uid, $bidAmount );
	$cmd->sendCommand( $comm );
	print $cmd . "\n";

	$comm = new CommunicationManager( REMOTE_HOST, REMOTE_PORT );
	$comm->connect();
	$cmd = new CommandUserInfo( $obj->uid );
	$cmd->sendCommand( $comm );
	print $cmd . "\n";
}

