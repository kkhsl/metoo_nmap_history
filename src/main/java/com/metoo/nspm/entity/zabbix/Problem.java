package com.metoo.nspm.entity.zabbix;

import com.metoo.nspm.core.domain.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Problem  extends IdEntity {

    private Integer objectid;
    private String name;
    private String deviceName;
    private String interfaceName;
    private String hostids;
    private Long clock;
    private String uuid;
    private String ip;
    private String severity; //问题当前严重性。 可用值： 0 - 未分类； 1 - 信息； 2 - 警告； 3 - 平均； 4 - 高； 5 - 灾难。
    private String event;
    private Integer suppressed;// 问题是否被抑制 0：问题处于正常状态 1：问题被抑制
    private Integer status;

}
