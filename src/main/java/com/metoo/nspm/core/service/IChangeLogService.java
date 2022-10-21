package com.metoo.nspm.core.service;

import com.metoo.nspm.dto.ChangeLogDto;
import com.metoo.nspm.entity.ChangeLog;
import com.github.pagehelper.Page;

public interface IChangeLogService {


    Page<ChangeLog> findBySelect(ChangeLogDto dto);

    int save(ChangeLog instance);
}
