package com.metoo.nspm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.metoo.nspm.dto.page.PageDto;
import com.metoo.nspm.entity.nspm.GatherAlarm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatherAlarmDTO extends PageDto<GatherAlarm> {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date time;
    private String deviceUuid;
    private String deviceInterface;
    private String ip;
    private String mac;
    private Integer type;

    private String remoteDeviceUuid;
    private String remoteDeviceInterface;
}
