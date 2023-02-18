package com.oneentropy.udp.pub.sub.model;

import lombok.*;
import reactor.core.publisher.Flux;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelStream<T> {

    public enum StreamType{
        SENDER,
        RECEIVER
    }

    private Flux<T> stream;
    private StreamType streamType;

}
