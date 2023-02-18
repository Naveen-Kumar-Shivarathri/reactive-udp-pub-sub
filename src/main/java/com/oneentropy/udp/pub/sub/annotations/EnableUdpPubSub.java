package com.oneentropy.udp.pub.sub.annotations;

import com.oneentropy.udp.pub.sub.config.UdpConf;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import({UdpConf.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnableUdpPubSub {

}
