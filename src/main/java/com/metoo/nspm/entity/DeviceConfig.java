package com.metoo.nspm.entity;

import com.metoo.nspm.core.domain.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceConfig extends IdEntity {

    private String name;
    private Long neId;
    private Long accessoryId;
    private String data;
}
