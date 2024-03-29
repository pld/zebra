[![zebra travis](https://secure.travis-ci.org/onaio/zebra.png?branch=master)](http://travis-ci.org/onaio/zebra)

## Zebra

![Zebra Logo](https://raw.githubusercontent.com/onaio/zebra/master/resources/public/img/zebra.png)

### A web application that connects to the Ona API.

This is a web application that uses [Ring][1] and
[Compojure][2].

You will first need [Leiningen][3] installed.

Download the project dependencies with:

    lein deps

To start a development web server using AWS, first add your credentials:

    lein -with-profile + pallet pallet add-service aws aws-ec2 "[your aws key]" "[your aws secret key]"
    
Going forward, you can uberjar and deploy the latest code with:

    lein up

Compile the project into an ubjer with a [Jetty][4] servlet container and
deploy using [Pallet][5]:

    lein deploy

[1]: https://github.com/mmcgrana/ring
[2]: https://github.com/weavejester/compojure
[3]: https://github.com/technomancy/leiningen
[4]: http://jetty.codehaus.org/jetty
[5]: http://palletops.com
