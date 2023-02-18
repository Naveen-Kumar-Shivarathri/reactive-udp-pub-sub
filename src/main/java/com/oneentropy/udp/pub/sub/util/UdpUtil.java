package com.oneentropy.udp.pub.sub.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.oneentropy.udp.pub.sub.model.NodeIdentifier;
import com.oneentropy.udp.pub.sub.model.UdpPacket;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class UdpUtil{

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static JsonNode convertStringToJsonNode(String data){
        if(data==null){
            log.error("Cannot convert null value to JsonNode");
            return JsonNodeFactory.instance.objectNode();
        }
        try {
            return OBJECT_MAPPER.readTree(data);
        } catch (JsonProcessingException e) {
            log.error("An exception occured while trying to convert the data to JsonNode tree, error:"+e.getMessage());
            if(log.isTraceEnabled())
                e.printStackTrace();
        }
        return JsonNodeFactory.instance.objectNode();
    }

    public static String convertUdpPacketToString(UdpPacket udpPacket){
        if(udpPacket==null)
            return "";
        try {
            return OBJECT_MAPPER.writeValueAsString(udpPacket);
        } catch (Exception e) {
            log.error("Encountered an exception converting UdpPacket to String, error:{}",e.getMessage());
            if(log.isTraceEnabled())
                e.printStackTrace();
            return "";
        }
    }

    public static UdpPacket addMetaInformation(UdpPacket udpPacket, NodeIdentifier nodeIdentifier){
        if(udpPacket==null)
            udpPacket = new UdpPacket();
        if(udpPacket.getMeta()==null){
            udpPacket.setMeta(new UdpPacket.Meta());
        }
        injectMetaFields(udpPacket.getMeta(), nodeIdentifier.getNodeIdentity());
        return udpPacket;
    }

    private static void injectMetaFields(UdpPacket.Meta meta, String nodeIdentity){
        if(meta.getOrigin()==null){
            meta.setOrigin(nodeIdentity);
        }
        if(meta.getTraceId()==null){
            meta.setTraceId(UUID.randomUUID().toString());
        }
        if(meta.getPktCrtTimestamp()==0){
            meta.setPktCrtTimestamp(System.currentTimeMillis());
        }
        if(meta.getPacketId()==null){
            meta.setPacketId(UUID.randomUUID().toString());
        }
    }

    public static UdpPacket deserializePacket(String message){
        if(message==null||message.equals(""))
            return null;
        UdpPacket udpPacket = null;
        long currentTime = System.currentTimeMillis();
        try {
            udpPacket = OBJECT_MAPPER.readValue(message, UdpPacket.class);
        } catch (JsonProcessingException e) {
            log.error("Encountered an error while deserializing the message, error:{}", e.getMessage());
            if(log.isTraceEnabled())
                e.printStackTrace();
        }
        long difference = currentTime-udpPacket.getMeta().getPktCrtTimestamp();
        log.debug("Latency:{}ms",difference);
        return udpPacket;
    }


}
