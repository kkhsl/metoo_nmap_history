package com.metoo.nspm.core.service;

import com.metoo.nspm.entity.UserRole;

import java.util.List;

public interface IUserRoleService {

    int batchAddUserRole(List<UserRole> userRoles);

    boolean deleteUserByRoleId(Long id);
}
