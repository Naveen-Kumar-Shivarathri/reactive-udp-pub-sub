package com.oneentropy.udp.pub.sub.service;

public interface UdpPublisher<T> {

    void publish(T data);

}
