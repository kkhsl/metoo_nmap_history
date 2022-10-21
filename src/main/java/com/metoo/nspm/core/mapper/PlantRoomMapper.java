package com.metoo.nspm.core.mapper;

import com.metoo.nspm.dto.PlantRoomDTO;
import com.metoo.nspm.entity.PlantRoom;
import com.metoo.nspm.vo.PlantRoomVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PlantRoomMapper {

    PlantRoom getObjById(Long id);

    List<PlantRoomVO> query(PlantRoom instance);

    List<PlantRoom> selectConditionQuery(PlantRoomDTO instance);

    Page<PlantRoom> findBySelectAndRack(PlantRoomDTO instance);

    Page<PlantRoom> selectObjByCard(Map params);

    List<PlantRoom> selectObjByMap(Map params);

    int save(PlantRoom instance);

    int update(PlantRoom instance);

    int delete(Long id);

    int batchDel(String ids);
}
