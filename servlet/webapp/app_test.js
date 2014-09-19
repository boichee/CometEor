//http://cometdaily.com/2008/05/15/the-many-shades-of-bayeuxcometd-2/
//http://cometdaily.com/2008/10/14/private-messages-with-cometd-chat/

(function($) {
		//	state machine that manages the connection/disconnection process,
		//	directing an Auction object. Writes status messages to message area.
	function AuctionManager() {
		var STATE_INIT = 1;
		var STATE_WAITING_FOR_LOGIN = 2;
		var STATE_WAITING_TO_CONNECT = 3;
		var STATE_CONNECTION_FAILED = 4;
		var STATE_WAITING_FOR_AUTHENTICATION = 5;
		var STATE_AUTHENTICATION_FAILED = 6;
		var STATE_CONNECTED = 7;
		var STATE_WAITING_TO_DISCONNECT = 8;
		var STATE_DISCONNECTED = 9;
		var state = STATE_INIT;
		var seconds = 0;
		var retryNumber = 0;
		var auction;	//	the object being managed
		var that = this;

		var tick = function() {
			if ( state == STATE_INIT ) {
				auction.announce( 'start up' );

				auction.login();

				state = STATE_WAITING_FOR_LOGIN;
			}
			else if ( state == STATE_WAITING_FOR_LOGIN ) {
				if ( auction.isConnected() ) {
/*
					$('#w4c-i-want-it-btn').after( '<div id="bidbutton" style="height:20px;width:180px">CLICK THIS</div><input type="text" id="bidamount" name="bidamount" />' );
					$('#bidbutton').click( auction.placeBid );
*/
					$('#bidbuttonarea').html( '<a id="w4c-i-want-it-btn" href="#"></a><input type="text" id="bidamount" name="bidamount" />' );
					$('#w4c-i-want-it-btn').click( auction.placeBid );
					state = STATE_CONNECTED;
				}
			}

			var endTimeMillis = 1000 * auction.getEndTime();
			var nowTimeMillis = (new Date()).getTime();
			//console.log( endTimeMillis + ' ' + nowTimeMillis );
			var remaining = 0;

			if ( window.console ) {
				console.log( 'endTimeMillis=' + (new Date(endTimeMillis)).formatDate("yyyy-MM-dd HH:mm") +
								', nowTimeMillis=' + (new Date(nowTimeMillis)).formatDate("yyyy-MM-dd HH:mm") );
			}

			if ( endTimeMillis > nowTimeMillis ) {
				remaining = endTimeMillis - nowTimeMillis + $.cometd.getExtension( 'timesync' ).getTimeOffset();
			}

			auction.showTimeRemaining( remaining );

			setTimeout( tick, 1000 );
		};

String.repeat = function(chr,count)
{    
    var str = ""; 
    for(var x=0;x<count;x++) {str += chr}; 
    return str;
}
String.prototype.padL = function(width,pad)
{
    if (!width ||width<1)
        return this;   
 
    if (!pad) pad=" ";        
    var length = width - this.length
    if (length < 1) return this.substr(0,width);
 
    return (String.repeat(pad,length) + this).substr(0,width);    
}    
String.prototype.padR = function(width,pad)
{
    if (!width || width<1)
        return this;        
 
    if (!pad) pad=" ";
    var length = width - this.length
    if (length < 1) this.substr(0,width);
 
    return (this + String.repeat(pad,length)).substr(0,width);
} 
		Date.prototype.formatDate = function(format) {
			var date = this;
			if (!format)
			  format="MM/dd/yyyy";               

			  var month = date.getMonth() + 1;
			var year = date.getFullYear();    
			format = format.replace("MM",month.toString().padL(2,"0"));        
			if (format.indexOf("yyyy") > -1)
				format = format.replace("yyyy",year.toString());
			else if (format.indexOf("yy") > -1)
				format = format.replace("yy",year.toString().substr(2,2));
			format = format.replace("dd",date.getDate().toString().padL(2,"0"));
			var hours = date.getHours();       
			if (format.indexOf("t") > -1){
			   if (hours > 11)
				format = format.replace("t","pm")
			   else
				format = format.replace("t","am")
			}
			if (format.indexOf("HH") > -1)
				format = format.replace("HH",hours.toString().padL(2,"0"));
			if (format.indexOf("hh") > -1) {
				if (hours > 12) hours - 12;
				if (hours == 0) hours = 12;
				format = format.replace("hh",hours.toString().padL(2,"0"));        
			}
			if (format.indexOf("mm") > -1)
			   format = format.replace("mm",date.getMinutes().toString().padL(2,"0"));
			if (format.indexOf("ss") > -1)
			   format = format.replace("ss",date.getSeconds().toString().padL(2,"0"));
			return format;
		}
		this.start = function( auc ) {
			auction = auc;
			state = STATE_INIT;
			tick();
		}
	}

    function Auction() {
        var _self = this;
        var _wasConnected = false;
        var _connected = false;
        var _username;
        var _password;
        var _lastUser;
        var _disconnecting;
        var _auctionSubscription;
        var _toplistSubscription;
		var _createUserSubscription;
		var _userID = 0;
		var _endTime = 0;
		var that = this;

		this.isConnected = function() {
			return _connected;
		}

		this.login = function() {
            _disconnecting = false;

//            var cometdURL = 'http://173.203.90.131:8080/win4c-app/cometd';
//            var cometdURL = 'http://173.231.136.98:8080/win4c-app/cometd';
            var cometdURL = 'http://cometEor.com:8080/win4c-app/cometd';

            $.cometd.configure({
                url: cometdURL,
                logLevel: 'debug'
            });

			$.cometd.handshake();
		}

		this.isAuthenticated = function() {
			return authenticated;
		}

		this.showTimeRemaining = function( timeDiff ) {
			var seconds_left = timeDiff / 1000;
			var seconds  = zeroPad( Math.floor(seconds_left / 1) % 60 );
			var minutes  = zeroPad( Math.floor(seconds_left / 60) % 60 );
			var hours    = zeroPad( Math.floor(seconds_left / 3600) % 24 );
			var days     = Math.floor(seconds_left / 86400) % 86400;
			$("#auction_time_remaining").text( '' + days + ' days ' + hours + ':' + minutes + ':' + seconds );
		}

		this.setBidsRemaining = function( msg ) {
			$("#auction_user_bids_remaining").text( msg );
		}

		this.announce = function( msg ) {
			$("#w4c-banner-status").text( msg );
		}

		this.getEndTime = function() {
			return _endTime;
		}

		function makeList( list ) {
			var s = '', obj, temp;

			for ( var i in list ) {
				obj = list[ i ];
				temp = obj.name + ' (' + obj.uid + ') has ' + obj.bids;
				s += '<li><a href="' + obj.link + '">' + temp + '</a></li>';
			}

			return '<ul>' + s + '</ul>';
		}

		this.cbUpdateTopList = function( message ) {
			if ( message && message !== undefined && message.data !== undefined ) {
				var data = message.data;
				if ( data.toplist !== undefined ) {
					var list = makeList( data.toplist );
					$('#w4c-banner-toplist').html( list );
					//alert( $.dump( data.toplist ) );
				}
			}
        };

        this.cbUpdateBidPrice = function( message ) {
			if ( message && message !== undefined && message.data !== undefined ) {
				var data = message.data;
				if ( data.price !== undefined ) {
					var p = '$' + new Number( 0.01 * data.price ).toFixed( 2 );
					$("#auction_current_bid").text( p );
				}
				if ( data.endTime !== undefined ) {
					_endTime = 1 * data.endTime;
				}
				if ( data.userName !== undefined ) {
					$('#w4c-banner-last-bidder').text( data.userName );
				}
			}
        };

		function cbAuction( message ) {
			if ( message && message !== undefined && message.data !== undefined ) {
				var data = message.data;
				if ( data.userbids !== undefined ) {
					$('#w4c-banner-wish-count').text( 'You have ' + data.userbids + ' wishes' );
					$('#cometEores_user_area dd').text( 'You have ' + data.userbids + ' wishes available.' );
				}
				if ( data.resultMessage !== undefined ) {
					that.announce( data.resultMessage );
				}
			}

//			alert( $.dump( message ) );
		}

        this.placeBid = function() {
            var jqBidamount = $('#bidamount');
            var bidamount = jqBidamount.val();
			if ( !bidamount || bidamount === undefined || bidamount.length < 1 ) {
				bidamount = 0;
			}

			var cookie = getDrupalSessionCookieValue();
			if ( !cookie ) {
				alert( 'no cookie' );
				return false;
			}

            jqBidamount.val('');

			that.announce( 'PLACING ' + bidamount );
			//alert( 'placing ' + bidamount );

			$.cometd.publish('/service/auction', {
				cookie: cookie,
				bid: bidamount
/*
				userID: _userID,
*/
			});

			return false;
		};

		function zeroPad( num ) {
			if ( num < 10 ) {
				return '0' + num;
			}
			else {
				return '' + num;
			}
		}

        function _connectionInitialized() {
			$.cometd.batch( function() {
				_auctionSubscription = $.cometd.subscribe( '/auction/bidprice', _self.cbUpdateBidPrice );
				_toplistSubscription = $.cometd.subscribe( '/auction/toplist', _self.cbUpdateTopList );
				//_createUserSubscription = $.cometd.publish( '/service/testing_createuser', { product: 'foo' } );
			});
			_self.announce( 'connection initialized' );
        }

        function _connectionEstablished() {
        }

        function _connectionBroken() {
			_self.announce( 'connection broken' );
        }

        function _connectionClosed() {
			_self.announce( 'connection closed' );
        }

        function _metaConnect(message) {
            if (_disconnecting) {
                _connected = false;
                _connectionClosed();
            }
            else {
                _wasConnected = _connected;
                _connected = message.successful === true;
                if (!_wasConnected && _connected) {
                    _connectionEstablished();
                }
                else if (_wasConnected && !_connected) {
					_self.announce( 'handshake received' );
                    _connectionBroken();
                }
            }
        }

        function _metaHandshake(message) {
            if (message.successful) {
				_self.announce( 'handshake received' );
                _connectionInitialized();
            }
			else {
				_self.announce( 'handshake failed' );
			}
        }

        $.cometd.addListener( '/meta/handshake', _metaHandshake );
        $.cometd.addListener( '/meta/connect', _metaConnect );
        $.cometd.addListener( '/service/auction', cbAuction );

        $(window).unload(function() {
			$.cometd.disconnect();
        });
    }

	function getDrupalSessionCookieValue() {
		if (document.cookie && document.cookie != '') {
			var cookies = document.cookie.split(';');
			for (var i = 0; i < cookies.length; i++) {
				var cookie = jQuery.trim(cookies[i]);
				// Does this cookie string begin with the name we want?
				if ( cookie.indexOf( 'SESS' ) === 0 && cookie.indexOf( '=' ) >= 0 ) {
					var val = cookie.substring( cookie.indexOf( '=' ) + 1 );
					val = decodeURIComponent( val );
					return val;
				}
			}
		}

		return null;
	}

    $(document).ready(function() {
        var auction = new Auction();

		var manager = new AuctionManager();

		manager.start( auction );
    });
})(jQuery);





/*
		var tick = function() {
			var now = $.cometd.getExtension( 'timesync' ).getServerDate();
			auction.showTimeRemaining( now.toUTCString() );
			now = now.getTime();
			var next = "" + (1 + now / 1000);
			next = parseInt(next.split('.')[0]) * 1000 + 10;
			$.cometd.getExtension( 'timesync' ).setTimeout(tick, next);
		};
		setTimeout( tick, 1000 );

			//	test
		setTimeout( function() {
			var bogusUsername = 'test' + ( Math.random() * 100000 );
			auction.join( bogusUsername, bogusUsername );
		}, 1000 + ( Math.random() * 10000 ) );
			//	test
*/
	//alert( 'browser ' + crc32( s ) );
//	alert( $.dump( $.cookie( 'SESS' ) ) );

/*
		$('#edit-submit').click( function() {
			var nm = $('#edit-name').val();
			var pwd = $('#edit-pass').val();
			alert( nm + ' ' + pwd );
		});

	var s = navigator.appCodeName + navigator.appName + navigator.appVersion + navigator.userAgent;


        $.cometd.addListener( '/service/testing_createuser', cbTestingCreateUser );
		function cbTestingCreateUser( message ) {
			if ( message && message !== undefined && message.data !== undefined ) {
				var data = message.data;
				if ( data.result !== undefined ) {
					_userID = data.result;
				}
			}

			$('#auction_user_id').text( _userID );
		}
*/

