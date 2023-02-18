package com.oneentropy.udp.pub.sub.model;

import com.oneentropy.udp.pub.sub.service.UdpPublisher;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChannelCollection {

    private Map<String, ChannelStream<?>> receiverMap;
    private Map<String, UdpPublisher<?>> publisherMap;

    public void addPublisher(String channelId, UdpPublisher<?> publisher){
        if(this.publisherMap==null)
            this.publisherMap = new HashMap<>();
        this.publisherMap.put(channelId, publisher);
    }

    public void addReceiver(String channelId, ChannelStream<?> channelStream){
        if(this.receiverMap ==null)
            this.receiverMap = new HashMap<>();
        this.receiverMap.put(channelId, channelStream);
    }

    public UdpPublisher<?> getPublisher(String channelId){
        if(this.publisherMap==null)
            return null;
        return this.publisherMap.get(channelId);
    }

    public ChannelStream<?> getChannelStream(String channelId){
        if(this.receiverMap==null)
            return null;
        return this.receiverMap.get(channelId);
    }

}
