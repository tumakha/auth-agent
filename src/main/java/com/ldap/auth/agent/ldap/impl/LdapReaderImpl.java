package com.ldap.auth.agent.ldap.impl;

import com.ldap.auth.agent.AppConfig;
import com.ldap.auth.agent.domain.Group;
import com.ldap.auth.agent.domain.LdapEntry;
import com.ldap.auth.agent.domain.User;
import com.ldap.auth.agent.ldap.LdapReader;
import com.ldap.auth.agent.ldap.mapper.GroupMapper;
import com.ldap.auth.agent.ldap.mapper.Mapper;
import com.ldap.auth.agent.ldap.mapper.UserMapper;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.ldap.auth.agent.domain.LdapEntryComparator.COMPARATOR;

/**
 * @author Yuriy Tumakha
 */
@Component
public class LdapReaderImpl implements LdapReader {

    private static final Logger LOG = LoggerFactory.getLogger(LdapReaderImpl.class);

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    public List<User> getUsers() {
        return searchEntry(userMapper);
    }

    public List<Group> getGroups() {
        return searchEntry(groupMapper);
    }

    private <T extends LdapEntry> List<T> searchEntry(Mapper<T> entryMapper) {
        try {
            List<T> entries = appConfig.getLdapConnectionTemplate().search( new Dn(entryMapper.getDnBase()),
                    entryMapper.getObjectFilter(), entryMapper.getSearchScope(), entryMapper );
            T nullEntry = entryMapper.getNullEntry();
            entries.removeIf(nullEntry::equals);
            Collections.sort(entries, COMPARATOR);
            return entries;

        } catch (LdapException e) {
            LOG.error("LDAP Exception: " + e.getMessage(), e);
        }
        return null;
    }

}