Ona viewer that connects to the Ona API.

This is a web application that uses [Ring][1] and
[Compojure][2].

You will first need [Leiningen][3] installed.

Download the project dependencies with:

    lein deps

Now you can start a development web server with:

    lein up

Or you can compile the project into a war-file ready for deployment to
a servlet container like [Tomcat][4] or [Jetty][5]:

    lein ring uberwar

[1]: https://github.com/mmcgrana/ring
[2]: https://github.com/weavejester/compojure
[3]: https://github.com/technomancy/leiningen
[4]: http://tomcat.apache.org
[5]: http://jetty.codehaus.org/jetty
