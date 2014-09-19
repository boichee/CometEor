<?php

$link = mysql_connect( 'localhost', 'cometEor_au7Ue', 'OI#@!$)(FDS' );
echo $link;
mysql_select_db( 'cometEor_auction' );

for ( $i = 0; $i < 10; $i++ ) {
	$bids = rand( 0, 100 );
	$nm = 'nm' . $bids;
	mysql_query( "INSERT INTO user(username,bidsAvailable) VALUES ( '$nm', $bids )" );
	echo mysql_error();
}

