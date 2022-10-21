package com.metoo.nspm.entity;

import com.metoo.nspm.core.domain.IdEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Subnet extends IdEntity {

    private String subnet;
    private String mask;
    private String description;
    private Long masterSubnetId;
    private String vlan;
    private String threshold;

}
