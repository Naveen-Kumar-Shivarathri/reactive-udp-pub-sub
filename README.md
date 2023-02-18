# Reactive UDP Pub/Sub

This library is an implementation of Publisher/Subscriber messaging pattern written using Reactive Programming Paradigm and UDP protocol.

## Tested features

* No of messages published/second (using 3 threads) = 71000
* No of messages received/second (1 thread) = 71000
* Latency = 0ms average

The above tests are performed in a local machine under following conditions:

* Machine is not connected to any local area network
* Multicast address used for publishing/receiving : 230.0.0.0
* 3 threads for publishing (3 instances each having 1 dedicated thread)
* 1 thread for receiving  (1 instance having 1 dedicated thread)
* CPU: Core i3 processor
* Each JVM instance has been allocated a default Heap memory of 256mb

---
## How to integrate?

Add the following annotation to any configuration component

````
@EnableUdpPubSub
````

Define properties as beans for:
<br>
<br>
UdpChannelProperties, which contains the Publishing and Receiving channel Host, port and other details
````
UdpChannelProperties
````
Typical configuration looks like below:
````
  udpChannelProperties:
    channelMap:
      RCVRCHNL1:
        ipAddress: ${RECEIVER_ADDR}
        port: ${RECEIVER_PORT}
        streamType: RECEIVER
      SNDRCHNL1:
        ipAddress: ${SENDER_ADDR}
        port: ${SENDER_PORT}
        streamType: SENDER
````
## Tutorial

Please follow below link for a quick tutorial

https://github.com/Naveen-Kumar-Shivarathri/simple-udp-client