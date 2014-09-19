CometEor
========

A CometD/Jetty server for getting around the C10K issue in web apps with bidirectional, always-open connections

### What is it?

CometEor is a web servlet intended to provide socket-based, bidirectional communication for high-demand, web applications that require continuous (and possibly high-bandwidth) communication with clients. To make it possible to communicate bidirectionally with a JS webapp, two HTTP connections are opened. The servlet can also allows multiple messages to be sent within a single HTTP response, avoiding some of the latency issues associated with reach new request. Based on my testing, the server should support well in excess of the usual 10K concurrent connections (see the [link: C10K problem](http://www.kegel.com/c10k.html)). 

Under the hood, CometEor uses the Bayeux protocol for socket-based connections. The servlet directory also includes a webapp written in JS/JQuery that establishes connections with this server. This system was initially built to support a real-time auction website, but now I've decided to pull this code out, and publish it here, to continue development for use in other projects.

A testing package, written in PHP is also included in the repository.
