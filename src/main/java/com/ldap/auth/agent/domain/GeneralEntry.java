package com.ldap.auth.agent.domain;

import java.util.Map;

/**
 * @author Yuriy Tumakha
 */
public class GeneralEntry extends LdapEntry {

    private String authId;
    private String dn;
    private Map<String, String> attributes;

    @Override
    public String getId() {
        return dn;
    }

    @Override
    public String getAuthId() {
        return authId;
    }

    @Override
    public void setAuthId(String authId) {
        this.authId = authId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneralEntry that = (GeneralEntry) o;

        if (dn != null ? !dn.equals(that.dn) : that.dn != null) return false;
        return attributes != null ? attributes.equals(that.attributes) : that.attributes == null;
    }

    @Override
    public int hashCode() {
        int result = dn != null ? dn.hashCode() : 0;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "GeneralEntry{" +
                "dn=" + dn +
                ", attributes=" + attributes +
                '}';
    }

}