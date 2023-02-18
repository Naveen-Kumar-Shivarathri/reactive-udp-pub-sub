package com.oneentropy.udp.pub.sub.config;

import com.oneentropy.udp.pub.sub.model.ChannelController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class UdpChannelControllerConf {

    @Autowired
    private UdpChannelProperties udpChannelProperties;

    @Bean
    public ChannelController createChannelController(){
        ChannelController chnlCntr = new ChannelController();
        chnlCntr.setRcvEventActivityMap(new HashMap<>());
        return chnlCntr;
    }

}
