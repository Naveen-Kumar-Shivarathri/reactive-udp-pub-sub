package com.oneentropy.udp.pub.sub.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.oneentropy.udp.pub.sub.model.ChannelController;
import com.oneentropy.udp.pub.sub.config.UdpChannelProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
@Slf4j
public class MockConf {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    @Value("classpath:udpChannelProperties.yml")
    private Resource udpChannelPropertiesFile;

    @Bean
    public UdpChannelProperties mockUdpChannelProperties(){
        return readChannelProperties();
    }


    @Bean
    public ChannelController mockChannelController(){
        return new ChannelController();
    }

    private UdpChannelProperties readChannelProperties() {
        try {
            UdpChannelProperties udpChannelProperties = OBJECT_MAPPER.readValue(udpChannelPropertiesFile.getFile(), UdpChannelProperties.class);
            return udpChannelProperties;
        } catch (IOException e) {
            log.error("Encountered an exception reading channel properties, error:{}", e.getMessage());
            return null;
        }
    }


}
