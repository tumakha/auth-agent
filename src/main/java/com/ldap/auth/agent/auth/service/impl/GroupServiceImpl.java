package com.ldap.auth.agent.auth.service.impl;

import com.ldap.auth.agent.auth.client.AuthClient;
import com.ldap.auth.agent.auth.service.GroupService;
import com.ldap.auth.agent.domain.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Service
public class GroupServiceImpl extends AuthServiceImpl<Group> implements GroupService {

    @Autowired
    private AuthClient authClient;

    @Override
    public List<Group> getAll() {
        return sort(authClient.getGroups());
    }

    @Override
    public void create(Group group) {
        authClient.createGroup(group);
    }

    @Override
    public void update(Group group) {
        authClient.updateGroup(group);
    }

    @Override
    public void delete(Group group) {
        authClient.deleteGroup(group);
    }

}