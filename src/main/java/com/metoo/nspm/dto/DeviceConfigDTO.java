package com.metoo.nspm.dto;

import com.metoo.nspm.dto.page.PageDto;
import com.metoo.nspm.entity.DeviceConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceConfigDTO extends PageDto<DeviceConfig> {
    private String name;
    private Long neId;
    private Long accessoryId;
}
