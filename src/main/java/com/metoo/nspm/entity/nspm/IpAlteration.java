package com.metoo.nspm.entity.nspm;

import com.metoo.nspm.core.domain.IdEntity;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@ApiModel("Ip变更记录")
@Data
@Accessors
@NoArgsConstructor
@AllArgsConstructor
public class IpAlteration extends IdEntity {

    private String ip;
    private String mac;

}
