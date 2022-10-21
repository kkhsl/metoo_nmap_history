package com.metoo.nspm.core.service.zabbix;

import com.metoo.nspm.dto.zabbix.ProblemDTO;

public interface ProblemService {

    Object get(ProblemDTO dto);
}
