package com.ldap.auth.agent.ldap.mapper;

import org.apache.directory.api.ldap.model.message.SearchScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author Yuriy Tumakha
 */
public class GroupMappings {

    private static final Logger LOG = LoggerFactory.getLogger(GroupMappings.class);

    private String groupsDN;

    private String groupsObjectFilter;

    private SearchScope searchScope;

    private String name;

    private String description;

    private String member;

    public String getGroupsDN() {
        return groupsDN;
    }

    public void setGroupsDN(String groupsDN) {
        this.groupsDN = groupsDN;
    }

    public String getGroupsObjectFilter() {
        return groupsObjectFilter;
    }

    public void setGroupsObjectFilter(String groupsObjectFilter) {
        if (StringUtils.isEmpty(groupsObjectFilter)) {
            groupsObjectFilter = "(objectclass=*)";
        }
        this.groupsObjectFilter = groupsObjectFilter;
    }

    public SearchScope getSearchScope() {
        return searchScope;
    }

    public void setSearchScope(SearchScope searchScope) {
        this.searchScope = searchScope;
    }

    public void setSearchScope(String searchScope) {
        try {
            this.searchScope = SearchScope.valueOf(searchScope);
        } catch (Exception ex) {
            LOG.warn("Wrong search scope name. " + ex.getMessage());
            this.searchScope = SearchScope.ONELEVEL;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

}