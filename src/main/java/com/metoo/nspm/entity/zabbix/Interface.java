package com.metoo.nspm.entity.zabbix;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Interface {

    private Long hostid;
    private String ip;
    private String available;// 0 - (默认) 未知; 1 - 可用; 2 - 不可用。
    private String error;
    private String value;
    private List<InterfaceTag> itemTags = new ArrayList();
}
