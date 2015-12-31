package com.ldap.auth.agent.ldap;

import com.ldap.auth.agent.domain.Group;
import com.ldap.auth.agent.domain.User;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
public interface LdapReader {

    List<User> getUsers();

    List<Group> getGroups();

}