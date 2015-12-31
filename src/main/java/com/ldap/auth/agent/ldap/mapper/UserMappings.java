package com.ldap.auth.agent.ldap.mapper;

import org.apache.directory.api.ldap.model.message.SearchScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Yuriy Tumakha
 */
public class UserMappings {

    private static final Logger LOG = LoggerFactory.getLogger(UserMappings.class);

    private String usersDN;

    private String usersObjectFilter;

    private SearchScope searchScope;

    private String userId;

    private String email;

    private String username;

    private String password;

    private String countryCode;

    private String firstName;

    private String lastName;

    private String phone;

    private String memberOf;

    private Set<String> syncGroupsDN;

    public String getUsersDN() {
        return usersDN;
    }

    public void setUsersDN(String usersDN) {
        this.usersDN = usersDN;
    }

    public String getUsersObjectFilter() {
        return usersObjectFilter;
    }

    public void setUsersObjectFilter(String usersObjectFilter) {
        if (StringUtils.isEmpty(usersObjectFilter)) {
            usersObjectFilter = "(objectclass=*)";
        }
        this.usersObjectFilter = usersObjectFilter;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(String memberOf) {
        this.memberOf = memberOf;
    }

    public Set<String> getSyncGroupsDN() {
        return syncGroupsDN;
    }

    public void setSyncGroupsDN(Set<String> syncGroupsDN) {
        this.syncGroupsDN = syncGroupsDN;
    }

    public void setSyncGroupsDN(String syncGroupsDN) {
        Set<String> groups = new HashSet<>();
        for (String group : syncGroupsDN.split(";")) {
            String dn = group.trim();
            if (!StringUtils.isEmpty(dn)) {
                groups.add(dn);
            }
        }
        this.syncGroupsDN = groups;
    }

}