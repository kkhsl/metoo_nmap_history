package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.entity.zabbix.Item;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ItemService {

    void gatherArpItem(Date time);

    void gatherMacItem(Date time);

    void gatherMacBatch(Date time);

    void gatherMacBatchStream(Date time);

    void gatherMacThreadPool(Date time);

    void gatherRouteItem(Date time);

    void gatherIpaddressItem(Date time);

    void gatherProblemItem(Date time);

    void gatherStp(Date time);

    void gathermstpinstance(Date time);

    void gathermstpDR(Date time);

    void writeStpRemote();

    /**
     * 根据条件查询Item 基础查询：根据Ip查询interface，确定一个hostid，根据hostid查询items表，确定itemids
     * 然后确定指定标签的itemid, 查询item_tag所有itemid相同的其他项
     * @return
     */
    List<Item> selectTagByMap(Map params);

    List<Item> selectItemTagByMap(Map params);

    void testTransactional();

    /**
     * 根据索引查询端口名（ifbasic，index）
     * @param index
     * @return
     */
    List<Item> selectNameObjByIndex(String index);

    /**
     * 同步拓扑连线关系到MacTemp
     */
    void topologySyncToMac();

    void topologySyncToMacBatch(Date time);

    // 查询端口状态
    Integer selectInterfaceStatus(Map params);


}
