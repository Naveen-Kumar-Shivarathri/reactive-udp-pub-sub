package com.oneentropy.udp.pub.sub.config;

import com.oneentropy.udp.pub.sub.model.*;
import com.oneentropy.udp.pub.sub.service.UdpPublisher;
import com.oneentropy.udp.pub.sub.util.UdpUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.*;
import java.util.UUID;

@Configuration
@NoArgsConstructor
@Slf4j
public class UdpConf {

    private static final String DEFAULT = "DEFAULT";


    @Autowired
    private UdpChannelProperties udpChannelProperties;

    @Autowired
    private ChannelController channelController;

    @Value("${one.entropy.reactive.udp.pub.sub.packet.length:512}")
    private int packetLength;

    @Value("${one.entropy.reactive.udp.pub.sub.node.name:DEFAULT}")
    private String nodeName;

    @Value("${one.entropy.reactive.udp.pub.sub.node.env:DEFAULT}")
    private String nodeEnv;

    @Bean
    public NodeIdentifier createNodeIdentifier(){
        String node = nodeName==DEFAULT? UUID.randomUUID().toString(): nodeName;
        return NodeIdentifier.builder().node(node).env(nodeEnv).build();
    }

    @Bean
    public ChannelCollection createChannelCollection() {

        ChannelCollection channelCollection = new ChannelCollection();
        udpChannelProperties.getChannelMap().forEach((channelId, channel) -> {
            log.info("Creating channel for:{}", channelId);
            Sinks.Many<UdpPacket> sink = Sinks.many().multicast().onBackpressureBuffer();
            Flux<UdpPacket> udpPacketFlux = sink.asFlux().share();
            if (channel.getStreamType() == ChannelStream.StreamType.RECEIVER) {
                handleReceiverStream(channelCollection, channelId, channel, sink, udpPacketFlux);
            } else {
                createPublisherStream(sink, channelId, channel, channelCollection);
            }

        });

        return channelCollection;
    }

    private void createPublisherStream(Sinks.Many<UdpPacket> sink, String channelId, UdpChannelProperties.Channel channel, ChannelCollection channelCollection){
        log.debug("Creating publishing stream:{}",channelId);
        UdpPublisher<?> publisher = (UdpPacket data)->{
            sink.tryEmitNext(data);
        };
        channelCollection.addPublisher(channelId,publisher);
        Flux<UdpPacket> udpPacketFlux = sink.asFlux();
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(channel.getIpAddress());
            NodeIdentifier nodeIdentifier = createNodeIdentifier();
            udpPacketFlux.subscribeOn(Schedulers.newSingle("sndr-"+channelId+":Thread")).subscribe(udpPacket -> {
                udpPacket = UdpUtil.addMetaInformation(udpPacket, nodeIdentifier);
                String message = UdpUtil.convertUdpPacketToString(udpPacket);
                DatagramPacket packet = new DatagramPacket(message.getBytes(),message.getBytes().length, address, channel.getPort());
                try {
                    log.debug("Sending packet");
                    socket.send(packet);
                } catch (IOException e) {
                    log.error("Encountered an exception publishing message, check internet connectivity, error:{}",e.getMessage());
                    if(log.isTraceEnabled())
                        e.printStackTrace();
                }
            });
        } catch (SocketException e) {
            log.error("Encountered an error creating socket for publisher, error:"+e.getMessage());
            if(log.isTraceEnabled())
                e.printStackTrace();
        }catch (UnknownHostException e) {
            log.error("Specified host name is not valid, error:{}", e.getMessage());
            if (log.isTraceEnabled())
                e.printStackTrace();
        } catch (Exception e) {
            log.error("Encountered an exception creating a new UDP socket, error:{}", e.getMessage());
            if (log.isTraceEnabled())
                e.printStackTrace();
        }

    }

    private void handleReceiverStream(ChannelCollection channelCollection, String channelId, UdpChannelProperties.Channel channel, Sinks.Many<UdpPacket> sink, Flux<UdpPacket> udpPacketFlux) {
        log.debug("Handling receiver stream:{}", channelId);
        attachFluxToReceiverLoop(sink, channel, channelId);
        ChannelStream<UdpPacket> channelStream = new ChannelStream<>();
        channelStream.setStream(udpPacketFlux);
        channelCollection.addReceiver(channelId, channelStream);
    }


    private void attachFluxToReceiverLoop(Sinks.Many<UdpPacket> sink, UdpChannelProperties.Channel channel, String channelId) {
        try {
            MulticastSocket socket = new MulticastSocket(channel.getPort());
            InetAddress bindAddress = InetAddress.getByName(channel.getIpAddress());
            socket.joinGroup(bindAddress);
            Mono.defer(() -> {
                byte[] messageBuffer = new byte[packetLength];
                while (channelController.allowedToRun(channelId)) {
                    DatagramPacket packet = new DatagramPacket(messageBuffer, messageBuffer.length);
                    try {
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        UdpUtil.calculateLatency(message);
                        log.debug("Received message:" + message);
                        UdpPacket udpPacket = UdpPacket.builder().data(UdpUtil.convertStringToJsonNode(message)).build();
                        sink.tryEmitNext(udpPacket);
                    } catch (IOException e) {
                        log.error("Encountered an exception reading message, error:{}", e.getMessage());
                        if (log.isTraceEnabled())
                            e.printStackTrace();
                    } catch (Exception e) {
                        log.error("Encountered an error while receiving message in event loop, error:{}", e.getMessage());
                        if (log.isTraceEnabled())
                            e.printStackTrace();
                    }

                }
                return Mono.empty();
            }).subscribeOn(Schedulers.newSingle("rcvr-" + channelId + ":Thread")).subscribe();

        } catch (SocketException e) {
            log.error("Encountered an exception creating a new UDP socket, error:{}", e.getMessage());
            if (log.isTraceEnabled())
                e.printStackTrace();
        } catch (UnknownHostException e) {
            log.error("Specified host name is not valid, error:{}", e.getMessage());
            if (log.isTraceEnabled())
                e.printStackTrace();
        } catch (Exception e) {
            log.error("Encountered an exception creating a new UDP socket, error:{}", e.getMessage());
            if (log.isTraceEnabled())
                e.printStackTrace();
        }

    }


}
