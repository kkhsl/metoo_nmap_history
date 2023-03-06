package com.metoo.nspm.entity.nspm;

import com.metoo.nspm.core.domain.IdEntity;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("生成树")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpanningTreeProtocol extends IdEntity {

    private String deviceName;
    private String deviceUuid;
    private String instance;
    private String vlan;
    private String root;
    private String ifRoot;
    private String portIndex;
    private String portName;
    private String portRole;
    private String portStatus;
    private String remoteDevice;
    private String remoteUuid;
    private String remotePort;

}
