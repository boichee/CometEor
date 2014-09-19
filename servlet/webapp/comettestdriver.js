CometTestDriverTest = {
	run : function() {
		this.timerID = setInterval( function() {
			$('#statusmessage').text( 'STATMSG ' + Math.random() );
			$('#auction_time_remaining').text( Math.floor( 100 * Math.random() ) );
			$('#auction_current_bid').text( Math.floor( 100 * Math.random() ) );
		},
		1000 );
	},
}

CometTestDriver = {
	currentMessage : 'NOERROR',
	currentBid : 0,
	bidsRemaining : 0,
	bidFrequencyModulus : 80,
	iteration : 0,
	timerDelay : 100,
	timerID : 0,

	run : function() {
		var that = this;

		this.logState( "I" );
		this.timerID = setInterval( function() {
			that.iteration++;
			var newMessage = that.getPageMessage();
			var newBid = that.getPageServerBid();
			var newRemaining = that.getPageBidsRemaining();
			if ( newMessage != that.currentMessage ||
					newBid != that.currentBid ||
					newRemaining != that.bidsRemaining ) {
				that.currentMessage = newMessage;
				that.currentBid = newBid;
				that.bidsRemaining = newRemaining;
				that.logState( "C" );
			}

			var randomDelta = 10 - Math.floor( Math.random() * 20 );
			var modulus = that.bidFrequencyModulus + randomDelta;
			//$('#debugging2').append( '<div>' + that.iteration + ' ' + modulus + '</div>' );
			if ( that.iteration % modulus == 0 ) {
				$('#debugging2').text( 'PLACING!!!!!! ' + modulus );
				that.placeBid();
			}
		},
		this.timerDelay );
	},

	placeBid : function() {
		var bid = this.getPageServerBid() * 1;
		var maximumBid = '';
		if ( Math.random() < 0.5 ) {
			maximumBid = bid + Math.floor( ( Math.random() * 20 ) - 2 );
		}

		$('#bidamount').val( maximumBid );
		$('#bidbutton').click();
		this.logState( "B" );
	},

	logState : function( msg ) {
		var ary = [
					msg,
					this.currentBid,
					this.currentMessage,
					this.bidsRemaining,
					this.iteration,
					this.getPageTimeRemaining(),
					this.getPageMessage(),
					this.getPageServerBid(),
					this.getPageClientBidAmount(),
					this.getPageBidsRemaining(),
					new Date().getTime() ];

		$('#debugging').append( '<div>' + ary.join( "^^" ) + '</div>' );
	},

	getPageMessage : function() {
		return $('#statusmessage').text();
	},

	getPageTimeRemaining : function() {
		return $('#auction_time_remaining').text();
	},

	getPageServerBid : function() {
		return $('#auction_current_bid').text();
	},

	getPageClientBidAmount : function() {
		return $('#auction_current_bid').text();
	},

	getPageBidsRemaining : function() {
		return $('#auction_user_bids_remaining').text();
	}
}

$(document).ready(function() {
	CometTestDriver.run();
//	CometTestDriverTest.run();
});

