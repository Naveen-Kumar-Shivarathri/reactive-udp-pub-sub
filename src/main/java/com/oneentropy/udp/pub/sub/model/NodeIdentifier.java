package com.oneentropy.udp.pub.sub.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NodeIdentifier {

    private String node;
    private String env;
    private String nodeId;

    public String getNodeIdentity(){
        if(nodeId==null){
            nodeId=node+":"+env;
        }
        return nodeId;
    }


}
