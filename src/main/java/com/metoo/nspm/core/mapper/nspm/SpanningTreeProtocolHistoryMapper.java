package com.metoo.nspm.core.mapper.nspm;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SpanningTreeProtocolHistoryMapper {

    public void truncateTable();

    void copyMacTemp();

}
