package com.ldap.auth.agent.domain;

/**
 * @author Yuriy Tumakha
 */
public abstract class LdapEntry {

    private String authId;

    public abstract String getId();

    public String getAuthId() {
        return authId;
    }
    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public int compareTo(LdapEntry ldapEntry) {
        String id1 = getKey(getId());
        String id2 = getKey(ldapEntry.getId());
        return id1.compareTo(id2);
    }

    private String getKey(String id) {
        return id == null ? "" : id;
    }

}