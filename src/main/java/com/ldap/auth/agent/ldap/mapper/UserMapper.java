package com.ldap.auth.agent.ldap.mapper;

import com.ldap.auth.agent.AppConfig;
import com.ldap.auth.agent.domain.User;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.search.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

import static org.apache.directory.ldap.client.api.search.ExpressionFilter.expression;
import static org.apache.directory.ldap.client.api.search.FilterBuilder.and;
import static org.apache.directory.ldap.client.api.search.FilterBuilder.equal;
import static org.apache.directory.ldap.client.api.search.FilterBuilder.or;

/**
 * @author Yuriy Tumakha
 */
@Component
public class UserMapper extends Mapper<User> {

    private static final Logger LOG = LoggerFactory.getLogger(UserMapper.class);
    private static final User NULL_USER = new User();

    @Autowired
    private AppConfig appConfig;

    @Override
    public User map(Entry entry) throws LdapException {
        User user = new User();
        UserMappings userMappings = appConfig.getUserMappings();
        copyStringAttribute(entry, userMappings.getUserId(), user::setUserId);
        copyStringAttribute(entry, userMappings.getEmail(), user::setEmail);
        copyStringAttribute(entry, userMappings.getUsername(), user::setUsername);
        copyStringAttribute(entry, userMappings.getPassword(), user::setPassword);
        copyStringAttribute(entry, userMappings.getCountryCode(), user::setCountryCode);
        copyStringAttribute(entry, userMappings.getFirstName(), user::setFirstName);
        copyStringAttribute(entry, userMappings.getLastName(), user::setLastName);
        copyStringAttribute(entry, userMappings.getPhone(), user::setPhone);
        copyListAttribute(entry, userMappings.getMemberOf(), user::setMembership);
        if (user.getId() == null) {
            LOG.info("User without ID was ignored {}. Entry {}", user, entry.getDn());
            user = NULL_USER;
        }
        return user;
    }

    @Override
    public String getDnBase() {
        return appConfig.getUserMappings().getUsersDN();
    }

    @Override
    public String getObjectFilter() {
        UserMappings userMappings = appConfig.getUserMappings();
        String objectFilter = userMappings.getUsersObjectFilter();
        String memberOf = userMappings.getMemberOf();
        Set<String> syncGroupsDN = userMappings.getSyncGroupsDN();
        if (!StringUtils.isEmpty(memberOf) && syncGroupsDN.size() > 0) {
            FilterBuilder[] groupFilters = syncGroupsDN.stream().map(group -> equal(memberOf, group))
                    .toArray(FilterBuilder[]::new);
            objectFilter = and(
                    expression(objectFilter),
                    or(groupFilters)
            ).toString();
            LOG.debug("Users objectFilter: {}", objectFilter);
        }
        return objectFilter;
    }

    @Override
    public SearchScope getSearchScope() {
        return appConfig.getUserMappings().getSearchScope();
    }

    @Override
    public User getNullEntry() {
        return NULL_USER;
    }

}