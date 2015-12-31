package com.ldap.auth.agent.auth.client;

import com.ldap.auth.agent.domain.Group;
import com.ldap.auth.agent.domain.User;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
public interface AuthClient {

    List<Group> getGroups();

    List<User> getUsers();

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(User user);

    void createGroup(Group group);

    void updateGroup(Group group);

    void deleteGroup(Group group);

}