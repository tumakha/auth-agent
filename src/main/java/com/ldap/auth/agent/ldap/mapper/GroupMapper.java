package com.ldap.auth.agent.ldap.mapper;

import com.ldap.auth.agent.AppConfig;
import com.ldap.auth.agent.domain.Group;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Yuriy Tumakha
 */
@Component
public class GroupMapper extends Mapper<Group> {

    private static final Logger LOG = LoggerFactory.getLogger(GroupMapper.class);
    private static final Group NULL_GROUP = new Group();

    @Autowired
    private AppConfig appConfig;

    @Override
    public Group map(Entry entry) throws LdapException {
        Group group = new Group();
        GroupMappings groupMappings = appConfig.getGroupMappings();
        copyStringAttribute(entry, groupMappings.getName(), group::setName);
        copyStringAttribute(entry, groupMappings.getDescription(), group::setDescription);
        copyListAttribute(entry, groupMappings.getMember(), group::setMembers);
        if (group.getId() == null) {
            LOG.info("Group without ID was ignored {}. Entry {}", group, entry.getDn());
            group = NULL_GROUP;
        }
        return group;
    }

    @Override
    public String getDnBase() {
        return appConfig.getGroupMappings().getGroupsDN();
    }

    @Override
    public String getObjectFilter() {
        return appConfig.getGroupMappings().getGroupsObjectFilter();
    }

    @Override
    public SearchScope getSearchScope() {
        return appConfig.getGroupMappings().getSearchScope();
    }

    @Override
    public Group getNullEntry() {
        return NULL_GROUP;
    }

}