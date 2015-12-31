package com.ldap.auth.agent.domain;

import java.util.Comparator;

/**
 * @author Yuriy Tumakha
 */
public class LdapEntryComparator implements Comparator<LdapEntry> {

    public static final LdapEntryComparator COMPARATOR = new LdapEntryComparator();

    @Override
    public int compare(LdapEntry o1, LdapEntry o2) {
        return o1.compareTo(o2);
    }

}