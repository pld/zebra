[![ona-viewer travis](https://secure.travis-ci.org/onaio/ona-viewer.png?branch=master)](http://travis-ci.org/onaio/ona-viewer)

## Zebra

### A web application that connects to the Ona API.

This is a web application that uses [Ring][1] and
[Compojure][2].

You will first need [Leiningen][3] installed.

Download the project dependencies with:

    lein deps

Now you can start a development web server with:

    lein up

Compile the project into an ubjer with a [Jetty][4] servlet container and
deploy using [Pallet][5]:

    lein deploy

[1]: https://github.com/mmcgrana/ring
[2]: https://github.com/weavejester/compojure
[3]: https://github.com/technomancy/leiningen
[4]: http://jetty.codehaus.org/jetty
[5]: http://palletops.com
