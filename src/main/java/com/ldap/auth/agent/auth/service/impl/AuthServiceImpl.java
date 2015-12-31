package com.ldap.auth.agent.auth.service.impl;

import com.ldap.auth.agent.domain.LdapEntry;

import java.util.Collections;
import java.util.List;

import static com.ldap.auth.agent.domain.LdapEntryComparator.COMPARATOR;

/**
 * @author Yuriy Tumakha
 */
public abstract class AuthServiceImpl<E extends LdapEntry> {

    protected List<E> sort(List<E> list) {
        if (list == null) {
            list = Collections.emptyList();
        } else {
            Collections.sort(list, COMPARATOR);
        }
        return list;
    }

}