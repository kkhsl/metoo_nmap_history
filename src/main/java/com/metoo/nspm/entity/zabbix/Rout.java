package com.metoo.nspm.entity.zabbix;

import com.metoo.nspm.core.domain.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("路由表")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rout extends IdEntity {

    private String mask;
    private String destination;
    private String cost;
    private String flags;
    private String nextHop;
    private String interfaceName;
    private String deviceName;
    @ApiModelProperty("设备Uuid")
    private String deviceUuid;
    private String proto;
    private IpAddress ipAddress;
}
