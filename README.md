CometEor
========

A CometD/Jetty server for getting around the C10K issue in web apps with synchronous connections

### What is it?

CometEor is a web servlet intended to support bidirectional communication with high-demand, web applications that require continuous communication with attached clients. The server will support well in excess of the usual 10K concurrent connections (see the [link: C10K problem](http://www.kegel.com/c10k.html)). 

CometEor uses the Bayeux protocol for communications and, for the moment, includes JS/JQuery code that communicates with the server via asynchronous methods (i.e, AJAX, $.Promise).

A testing package, written in PHP is also included in the repository.
