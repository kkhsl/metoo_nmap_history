package com.metoo.nspm.core.service;

import com.metoo.nspm.dto.TopoCredentialDto;
import com.metoo.nspm.entity.Credential;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface ICredentialService {

    Credential getObjById(Long id);

    Credential getObjByName(String name);

    List<Credential> query();

    int save(Credential instance);

    int update(Credential instance);

    int delete(Long id);

    Map<String, String> getUuid(TopoCredentialDto dto);

    Page<Credential> getObjsByLevel(Credential instance);


}
