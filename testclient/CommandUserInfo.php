<?php

require_once( 'CommandBase.php' );

class CommandUserInfo extends CommandBase {
	private $uid;

	public function __construct( $uid ) {
		$this->uid = $uid;
		$this->result = self::NORESPONSE;
		$this->requestID = 0;
	}

	protected function createCommandString() {
		return "userinfo " . $this->uid;
	}

	protected function parseResultString( $res ) {
		if ( !$res ) {
			return $this->setGeneralError();
		}

		$ary = explode( ' ', $res );
		if ( !$ary || count( $ary ) < 2 ) {
			return $this->setGeneralError();
		}

		$this->requestID = $this->parseRequestID( $ary[ 1 ] );

		if ( !$this->requestID ) {
			return $this->setGeneralError();
		}

		$this->result = $this->parseResultCode( $ary[ 0 ] );

		return $this->resultIsSuccesful();
	}

	protected function parseRequestID( $s ) {
		if ( !ctype_digit( $s ) ) {
			return 0;
		}
		
		return 1 * $s;
	}

	public function __toString() {
		$s = "CommandUserInfo: uid=" . $this->uid;
		if ( $this->resultIsSuccesful() ) {
			 $s .= " - OK: res=" . $this->getResult() . "\n";
		}
		else {
			 $s .= " - ERROR: res=" . $this->getResult() .
					", code=" . $this->getErrorCode() .
					", msg=" . $this->getErrorMessage() . " \n";
		}
		
		return $s;
	}

	private function setGeneralError() {
			$this->result = self::UNKNOWN;
			$this->errorCode = -1;

			return FALSE;
	}
}
