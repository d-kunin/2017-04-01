# Introduction
Non-blocking server and client implementations and a messaging system build on top of them.
You plug-in you implementations of [Message](core/src/main/java/net/kundzi/messagesystem/protocol/MessageV2.java), [MessageReader](core/src/main/java/net/kundzi/messagesystem/io/MessageV2Reader.java), and [MessageWriter](core/src/main/java/net/kundzi/messagesystem/io/MessageV2Writer.java) and that's it.
In the messaging system messages are length-prefixed JSON objects. 

# How to run
You need to build uber-jars of backend and frontend first:

`mvn clean package`

After that running `ru.otus.kunin.app.Main.main` in IDEA will spin up 5 process:
* MessagingSystemService process bind to `localhost:9100`
* 2 Frontend process bind to `localhost:8091` and `localhost:8092`
* 2 Backend process

Now you can open `http://localhost:8091/cache.html` or `http://localhost:8092/cache.html` in Chrome
and see how nice it works.

# Important parts
* Non-blocking server [net.kundzi.nbserver](core/src/main/java/net/kundzi/nbserver)
    * Protocol independent server [net.kundzi.nbserver.server.SimpleReactorServer](core/src/main/java/net/kundzi/nbserver/server/SimpleReactorServer.java)
    * Protocol independent client [net.kundzi.nbserver.client.NonBlockingClient](core/src/main/java/net/kundzi/nbserver/server/SimpleReactorServer.java)
* Messaging system on top of the server [net.kundzi.messagesystem](core/src/main/java/net/kundzi/messagesystem)
    * Message model [net.kundzi.messagesystem.protocol.MessageV2](core/src/main/java/net/kundzi/messagesystem/protocol/MessageV2.java)
* Frontend - html + js + websocket [ru.otus.kunin.front](frontend/src/main/java/ru/otus/kunin)
* Backend - simple string-to-string cache [ru.otus.kunin.backend](backend/src/main/java/ru/otus/kunin/backend)


 