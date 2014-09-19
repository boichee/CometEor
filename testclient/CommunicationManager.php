<?php

class CommunicationManager {
	private $socket, $remoteAddress, $remotePort;

	public function __construct( $remoteAddress, $remotePort ) {
		$this->remoteAddress = $remoteAddress;
		$this->remotePort = $remotePort;
	}

	public function connect() {
		$this->socket = socket_create( AF_INET, SOCK_STREAM, SOL_TCP );
		if ( !$this->socket ) {
			echo 'CANNOT CREATE SOCKET1 ' . socket_strerror( socket_last_error() );
			return FALSE;
		}
		
		$bRes = socket_connect( $this->socket, $this->remoteAddress, $this->remotePort );
		if ( !$bRes ) {
			echo 'CANNOT CREATE SOCKET2 ' . socket_strerror( socket_last_error() );
		}

		return $bRes;
	}

	public function send( $str ) {
		echo 'GOT SOCKET2 ' . $this->socket;
		if ( !$this->socket ) {
			return FALSE;
		}

		$time_start = microtime(true);

			//	http://www.php.net/manual/en/function.socket-write.php
		$len = strlen( $str );
		$offset = 0;
		while ( $offset < $len ) {
			$sent = socket_write( $this->socket, substr( $str, $offset ), $len - $offset );
			if ( $sent === FALSE ) {
				// Error occurred, break the while loop
				break;
			}

			$offset += $sent;
		}

		if ( $offset < $len ) {
			return $this->makeErrorArray();
		}
		else {
			$result = socket_read( $this->socket, 1024 );
			if ( !$result ) {
				return $this->makeErrorArray();
			}

		echo "GOT " . $result;
			return array(
				'result' => trim( $result ),
				'time' => microtime(true) - $time_start,
			);
		}
	}

	protected function makeErrorArray() {
		return array(
			'errorcode' => socket_last_error(),
			'errormsg' => socket_strerror( $errorcode ),
			'time' => microtime(true) - $time_start,
		);
	}
}

