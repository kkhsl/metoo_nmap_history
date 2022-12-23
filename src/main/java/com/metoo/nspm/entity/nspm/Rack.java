package com.metoo.nspm.entity.nspm;

import com.metoo.nspm.core.domain.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("机柜")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rack extends IdEntity {

    @ApiModelProperty("机柜名称")
    private String name;
    @ApiModelProperty("机柜大小")
    private Integer size;
    private int surplusSize;
    @ApiModelProperty("是否显示背面")
    private Boolean rear;
    @ApiModelProperty("机房")
    private Long plantRoomId;
    @ApiModelProperty("机房名称")
    private String plantRoomName;
    @ApiModelProperty("用户")
    private Long userId;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("设备数量")
    private Integer number;

    @ApiModelProperty("资产编号")
    private String asset_number;
    @ApiModelProperty("变更原因")
    private String change_reasons;

}
