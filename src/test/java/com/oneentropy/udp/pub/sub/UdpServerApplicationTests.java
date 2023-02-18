package com.oneentropy.udp.pub.sub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.oneentropy.udp.pub.sub.conf.MockConf;
import com.oneentropy.udp.pub.sub.config.UdpChannelProperties;
import com.oneentropy.udp.pub.sub.config.UdpConf;
import com.oneentropy.udp.pub.sub.model.ChannelCollection;
import com.oneentropy.udp.pub.sub.model.UdpPacket;
import com.oneentropy.udp.pub.sub.service.UdpPublisher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes={UdpConf.class, MockConf.class})
@Slf4j
@ActiveProfiles(profiles = {"test"})
//@Import(AnnotationAwareAspectJAutoProxyCreator.class)
class UdpServerApplicationTests {


    @Autowired
    private UdpChannelProperties udpChannelProperties;

    @Autowired
    private ChannelCollection channelCollection;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    @Test
    void contextLoads() {
        log.info(udpChannelProperties.toString());
        Map<String, String> data = new HashMap<>();


        while(true){
            data.put("eventTime",System.currentTimeMillis()+"");
            data.put("message", "Hello there");
            JsonNode jsonNode = null;
            try {
                String json = OBJECT_MAPPER.writeValueAsString(data);
                jsonNode = OBJECT_MAPPER.readTree(json);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
            UdpPublisher<UdpPacket> sndrchnl1 = (UdpPublisher<UdpPacket>) channelCollection.getPublisher("SNDRCHNL1");
            UdpPacket udpPacket = UdpPacket.builder().data(jsonNode).build();
            sndrchnl1.publish(udpPacket);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }


}
