<?php

abstract class CommandBase {
	const NORESPONSE = 0;
	const UNKNOWN = 1;
	const CONNECTION_ERROR = 2;
	const TIMEOUT = 3;
	const PROCESSING = 4;
	const PROCESSED = 5;
	const BUFFER_FULL = 6;
	const NOT_ENOUGH_BIDS_AVAILABLE = 7;
	const REQUESTED_PRICE_TOO_LOW = 8;
	const PROCESSING_ERROR = 9;

	protected $result = self::NORESPONSE,
				$errorCode = 0,
				$errorMsg = '',
				$requestID = 0,
				$roundTripTime;

	abstract protected function createCommandString();
	abstract protected function parseResultString( $res );

	public function sendCommand( $communicationManager ) {
		$cmd = $this->createCommandString();

		$ary = $communicationManager->send( $cmd );

		$this->roundTripTime = $ary[ 'time' ];

		if ( isset( $ary[ 'errorcode' ] ) || isset( $ary[ 'errormsg' ] ) || !isset( $ary[ 'result' ] ) ) {
			$this->result = self::UNKNOWN;
			$this->errorCode = $ary[ 'errorcode' ] ? $ary[ 'errorcode' ] : -1;
			$this->errorMsg = $ary[ 'errormsg' ];

			return FALSE;
		}

		return $this->parseResultString( $ary[ 'result' ] );
	}

	public function resultIsSuccesful() {
		return ( $this->result == self::PROCESSING || $this->result == self::PROCESSED );
	}

	public function getResult() { return $this->result; }
	public function getErrorCode() { return $this->errorCode; }
	public function getErrorMessage() { return $this->errorMsg; }
	
	protected function parseResultCode( $code ) {
		$codes = getCommandCodes();

		if ( isset( $codes[ $code ] ) ) {
			return $codes[ $code ];
		}

		return self::UNKNOWN;
	}

	protected function getCommandCodes() {
		return array(
			'connecterror' => self::CONNECTION_ERROR,
			'processing' => self::PROCESSING,
			'processed' => self::PROCESSED,
			'fullbuffer' => self::BUFFER_FULL,
			'notenoughbids' => self::NOT_ENOUGH_BIDS_AVAILABLE,
			'pricetoolow' => self::REQUESTED_PRICE_TOO_LOW,
			'processingerror' => self::PROCESSING_ERROR,
		);
	}
}

