package com.oneentropy.udp.pub.sub.model;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelController {

    private Map<String, Boolean> rcvEventActivityMap;

    public boolean allowedToRun(String channelId){
        if(this.rcvEventActivityMap==null)
            return true;
        if(rcvEventActivityMap.containsKey(channelId)){
            return rcvEventActivityMap.get(channelId);
        }
        return true;
    }

}
