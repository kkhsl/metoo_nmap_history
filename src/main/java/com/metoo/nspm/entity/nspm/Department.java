package com.metoo.nspm.entity.nspm;

import com.metoo.nspm.core.domain.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@ApiModel("部门")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Department extends IdEntity {

    private String name;
    private String desc;
    private Long parentId;
    private Integer sequence;


    @ApiModelProperty("子集")
    private List<Department> branchList;
}
