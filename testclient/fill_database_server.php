<?php

$link = mysql_connect( 'localhost', 'win4caus_au7Ue', 'OI#@!$)(FDS' );
echo $link;
mysql_select_db( 'win4caus_auction' );

for ( $i = 0; $i < 10; $i++ ) {
	$bids = rand( 0, 100 );
	$nm = 'nm' . $bids;
	mysql_query( "INSERT INTO user(username,bidsAvailable) VALUES ( '$nm', $bids )" );
	echo mysql_error();
}

