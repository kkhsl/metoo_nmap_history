package com.metoo.nspm.entity.nspm;

import com.metoo.nspm.core.domain.IdEntity;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("部门")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department extends IdEntity {

    private String name;
    private String desc;
    private Long parentId;
    private Integer sequence;
}
