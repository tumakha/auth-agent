package com.ldap.auth.agent.auth.service;

import com.ldap.auth.agent.domain.LdapEntry;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
public interface AuthService<E extends LdapEntry> {

    List<E> getAll();

    void create(E entry);

    void update(E entry);

    void delete(E entry);

}