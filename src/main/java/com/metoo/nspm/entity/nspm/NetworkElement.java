package com.metoo.nspm.entity.nspm;

import com.metoo.nspm.core.domain.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors
@AllArgsConstructor
@NoArgsConstructor
public class NetworkElement extends IdEntity {

    private String filter;
    private String ip;
    private String deviceName;
    private String interfaceName;
    private Long groupId;
    private String groupName;
    private Long deviceTypeId;
    private String deviceTypeName;
    private DeviceType deviceType;
    private Long vendorId;
    private String vendorName;
    private String description;
    private Long userId;
    private String userName;
    private boolean sync_device;
    private String available;
    private String error;
    private String uuid;
    private String interfaceNames;
    private String flux;


}
