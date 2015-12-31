package com.ldap.auth.agent.task.impl;

import com.ldap.auth.agent.domain.Group;
import com.ldap.auth.agent.domain.User;
import com.ldap.auth.agent.ldap.LdapReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuriy Tumakha
 */
public class LdapReaderMock implements LdapReader {

    private List<User> users = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public List<Group> getGroups() {
        return groups;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

}