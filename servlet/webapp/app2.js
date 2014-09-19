(function($)
{
    $(document).ready(function()
    {
        // Check if there was a saved application state
        var stateCookie = org.cometd.COOKIE?org.cometd.COOKIE.get('org.cometd.demo.state'):null;
        var state = stateCookie ? org.cometd.JSON.fromJSON(stateCookie) : null;
        var auction = new Auction(state);

        // Setup UI
        $('#loginarea').show();
        $('#loggedin').hide();
        $('#loginbutton').click(function() {
			auction.join( $('#username').val(), $('#password').val() );
		});
        $('#bidbutton').click(auction.placeBid);
        $('#leaveButton').click(auction.leave);
        $('#username').attr('autocomplete', 'off').focus();
        $('#username').keyup(function(e)
        {
            if (e.keyCode == 13)
            {
                auction.join($('#username').val());
            }
        });
        $('#phrase').attr('autocomplete', 'off');
        $('#phrase').keyup(function(e)
        {
            if (e.keyCode == 13)
            {
                auction.placeBid();
            }
        });

		var tick = function()
		{
			var now = $.cometd.getExtension( 'timesync' ).getServerDate();
			$("#auction_time_remaining").text( now.toUTCString() );
			now = now.getTime();
			var next = "" + (1 + now / 1000);
			next = parseInt(next.split('.')[0]) * 1000 + 10;
			$.cometd.getExtension( 'timesync' ).setTimeout(tick, next);
		};
		setTimeout(tick, 1000);
    });

    function Auction(state)
    {
        var _self = this;
        var _wasConnected = false;
        var _connected = false;
        var _username;
        var _password;
        var _lastUser;
        var _disconnecting;
        var _auctionSubscription;
        var _membersSubscription;

        this.join = function( username, password )
        {
            _disconnecting = false;
            _username = username;
            _password = password;
            if ( !_username || !_password ) {
                alert('Please enter a username');
                return;
            }

            var cometdURL = location.protocol + "//" + location.host + config.contextPath + "/cometd";

            $.cometd.configure({
                url: cometdURL,
                logLevel: 'debug'
            });
            $.cometd.handshake();

            $('#loginarea').hide();
            $('#loggedin').show();
            $('#phrase').focus();
        };

        this.leave = function()
        {
            $.cometd.batch(function()
            {
                $.cometd.publish('/auction/demo', {
                    user: _username,
                    auction: _username + ' has left'
                });
                _unsubscribe();
            });
            $.cometd.disconnect();

            $('#loginarea').show();
            $('#loggedin').hide();
            $('#username').focus();
            $('#members').empty();
            _username = null;
			_password = null;
            _lastUser = null;
            _disconnecting = true;
        };

        this.placeBid = function() {
            var jqBidamount = $('#bidamount');
            var bidamount = jqBidamount.val();
            jqBidamount.val('');

			alert( 'placing ' + bidamount );

			$.cometd.publish('/service/auction', {
				user: _username,
				bid: bidamount
			});
        };

        this.receive = function(message)
        {
		alert('received ' + message );
            var fromUser = message.data.user;
            var membership = message.data.membership;
            var text = message.data.auction;

            if (!membership && fromUser == _lastUser)
            {
                fromUser = '...';
            }
            else
            {
                _lastUser = fromUser;
                fromUser += ':';
            }

            var auction = $('#auction');
            if (membership)
            {
                auction.append('<span class=\"membership\"><span class=\"from\">' + fromUser + '&nbsp;</span><span class=\"text\">' + text + '</span></span><br/>');
                _lastUser = null;
            }
            else if (message.data.scope == 'private')
            {
                auction.append('<span class=\"private\"><span class=\"from\">' + fromUser + '&nbsp;</span><span class=\"text\">[private]&nbsp;' + text + '</span></span><br/>');
            }
            else
            {
                auction.append('<span class=\"from\">' + fromUser + '&nbsp;</span><span class=\"text\">' + text + '</span><br/>');
            }

            // There seems to be no easy way in jQuery to handle the scrollTop property
            auction[0].scrollTop = auction[0].scrollHeight - auction.outerHeight();
        };

        /**
         * Updates the members list.
         * This function is called when a message arrives on channel /auction/members
         */
        this.members = function(message)
        {
            var list = '';
            $.each(message.data, function()
            {
                list += this + '<br />';
            });
            $('#members').html(list);
        };

        function _unsubscribe()
        {
            if (_auctionSubscription)
            {
                $.cometd.unsubscribe(_auctionSubscription);
            }
            _auctionSubscription = null;
            if (_membersSubscription)
            {
                $.cometd.unsubscribe(_membersSubscription);
            }
            _membersSubscription = null;
        }

        function _subscribe()
        {
            _auctionSubscription = $.cometd.subscribe('/auction/bidprice', _self.receive);
            _membersSubscription = $.cometd.subscribe('/auction/members', _self.members);
        }

        function _connectionInitialized()
        {
            // first time connection for this client, so subscribe tell everybody.
            $.cometd.batch(function()
            {
                _subscribe();
                $.cometd.publish('/auction/demo', {
                    user: _username,
                    membership: 'join',
                    auction: _username + ' has joined'
                });
            });
        }

        function _connectionEstablished()
        {
            // connection establish (maybe not for first time), so just
            // tell local user and update membership
            _self.receive({
                data: {
                    user: 'system',
                    auction: 'Connection to Server Opened'
                }
            });
            $.cometd.publish('/service/members', {
                user: _username,
                room: '/auction/demo'
            });
        }

        function _connectionBroken()
        {
            _self.receive({
                data: {
                    user: 'system',
                    auction: 'Connection to Server Broken'
                }
            });
            $('#members').empty();
        }

        function _connectionClosed()
        {
            _self.receive({
                data: {
                    user: 'system',
                    auction: 'Connection to Server Closed'
                }
            });
        }

        function _metaConnect(message)
        {
            if (_disconnecting)
            {
                _connected = false;
                _connectionClosed();
            }
            else
            {
                _wasConnected = _connected;
                _connected = message.successful === true;
                if (!_wasConnected && _connected)
                {
                    _connectionEstablished();
                }
                else if (_wasConnected && !_connected)
                {
                    _connectionBroken();
                }
            }
        }

        function _metaHandshake(message)
        {
		alert('hs');
            if (message.successful)
            {
		alert('hs success');
                _connectionInitialized();
            }
        }

        $.cometd.addListener('/meta/handshake', _metaHandshake);
        $.cometd.addListener('/meta/connect', _metaConnect);

        // Restore the state, if present
        if (state)
        {
            setTimeout(function()
            {
                // This will perform the handshake
                _self.join(state.username);
            }, 0);
        }

        $(window).unload(function() {
			$.cometd.disconnect();
        });
    }

})(jQuery);
