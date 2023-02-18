package com.oneentropy.udp.pub.sub.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UdpPacket{

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meta{
        private String traceId;
        private String origin;
        private String packetId;
        private long pktCrtTimestamp;
    }

    private Meta meta;
    private JsonNode data;
}
