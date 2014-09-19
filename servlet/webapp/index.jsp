<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery-1.4.2.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery.json-2.2.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/org/cometd.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery.cometd.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/org/cometd/TimeSyncExtension.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery.cometd-timesync.js"></script>

    <script type="text/javascript" src="jquery.dump.js"></script>
    <script type="text/javascript" src="app_test.js"></script>
    <script type="text/javascript" src="comettestdriver.js"></script>
    <%--
    The reason to use a JSP is that it is very easy to obtain server-side configuration
    information (such as the contextPath) and pass it to the JavaScript environment on the client.
    --%>
    <script type="text/javascript">
        var config = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>
	<style>
		.updatedarea,
		.bidamountarea,
		.bidsremainingarea,
		.statusmessagearea,
		.removethis,
		.bidscirclearea {
			border:1px solid black;
			margin-bottom:10px;
		}
	</style>
</head>
<body>

<div class="putstyleanddivshere">
	<div class="putstyleanddivshere">
		<div id="block--" class="clear-block block block-">

	<div class="putlogohere"></div>

	<div class="updatedarea">
		Time remaining:
		<div id="auction_time_remaining"></div>
	</div>

	<div class="updatedarea">
		Current bid:
		<div id="auction_current_bid"></div>
	</div>

	<div class="updatedarea">
		User ID:
		<div id="auction_user_id"></div>
	</div>

	<div class="bidamountarea">
		<input type="text" id="bidamount" />
	</div>

	<div class="bidbuttonarea">
		<a id="bidbutton" href="#">Bid for Cause</a>
	</div>

	<div class="bidsremainingarea">
		You have <span id="auction_user_bids_remaining"></span> bids
	</div>

	<div class="statusmessagearea">
		<span id="statusmessage"></span>
	</div>

	<div class="removethis">
		<div id="debugging"></div>
	</div>

	<div class="removethis">
		<div id="debugging2"></div>
	</div>

	<div class="bidscirclearea">
		The bidder's circle
		<div class="auction_top_bidders">
			updated by Javascript
		</div>
	</div>

</div>

<!--
    <script type="text/javascript" src="${pageContext.request.contextPath}/org/cometd/AckExtension.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery.cometd-ack.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/org/cometd/ReloadExtension.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery.cometd-reload.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/org/cometd/TimeStampExtension.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery.cometd-timestamp.js"></script>
-->

</body>
</html>
