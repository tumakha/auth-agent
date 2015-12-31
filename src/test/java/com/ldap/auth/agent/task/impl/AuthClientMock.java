package com.ldap.auth.agent.task.impl;

import com.ldap.auth.agent.auth.client.AuthClient;
import com.ldap.auth.agent.domain.Group;
import com.ldap.auth.agent.domain.User;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuriy Tumakha
 */
public class AuthClientMock implements AuthClient {

    private List<User> users = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();

    @Override
    public List<Group> getGroups() {
        List<Group> copy = new ArrayList<>(groups.size());
        copy.addAll(groups);
        return copy;
    }

    @Override
    public List<User> getUsers() {
        List<User> copy = new ArrayList<>(users.size());
        copy.addAll(users);
        return copy;
    }

    @Override
    public void createUser(User user) {
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        users.add(newUser);
    }

    @Override
    public void updateUser(User user) {
        for (User dbUser : users) {
            if (dbUser.getId().equals(user.getId())) {
                BeanUtils.copyProperties(user, dbUser);
            }
        }
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user);
    }

    @Override
    public void createGroup(Group group) {
        Group newGroup = new Group();
        BeanUtils.copyProperties(group, newGroup);
        groups.add(newGroup);
    }

    @Override
    public void updateGroup(Group group) {
        for (Group dbGroup : groups) {
            if (dbGroup.getId().equals(group.getId())) {
                BeanUtils.copyProperties(group, dbGroup);
            }
        }
    }

    @Override
    public void deleteGroup(Group group) {
        groups.remove(group);
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

}