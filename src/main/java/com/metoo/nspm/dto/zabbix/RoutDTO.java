package com.metoo.nspm.dto.zabbix;

import com.metoo.nspm.dto.page.PageDto;
import com.metoo.nspm.entity.LiveRoom;
import com.metoo.nspm.entity.zabbix.IpAddress;
import com.metoo.nspm.entity.zabbix.Rout;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class RoutDTO extends  PageDto<Rout> {
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
