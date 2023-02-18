package com.oneentropy.udp.pub.sub.config;

import com.oneentropy.udp.pub.sub.model.ChannelStream;
import lombok.*;

import java.util.Map;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class UdpChannelProperties{

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Channel{
        private String ipAddress;
        private int port;
        private ChannelStream.StreamType streamType;


    }

    private Map<String, Channel> channelMap;


}
