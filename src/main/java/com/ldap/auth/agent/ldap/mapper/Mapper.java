package com.ldap.auth.agent.ldap.mapper;

import com.ldap.auth.agent.domain.LdapEntry;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.template.EntryMapper;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Yuriy Tumakha
 */
public abstract class Mapper<T extends LdapEntry> implements EntryMapper<T> {

    protected void copyStringAttribute(Entry entry, String attributeName, Consumer<String> setter)
            throws LdapInvalidAttributeValueException {
        if (StringUtils.isEmpty(attributeName)) {
            return;
        }
        Attribute attribute = entry.get(attributeName);
        if (attribute != null) {
            String value = attribute.isHumanReadable() ?
                    attribute.getString() : new String(attribute.getBytes(), StandardCharsets.UTF_8);
            setter.accept(value);
        }
    }

    protected void copyListAttribute(Entry entry, String attributeName, Consumer<List<String>> setter)
            throws LdapInvalidAttributeValueException {
        if (StringUtils.isEmpty(attributeName)) {
            return;
        }
        Attribute attribute = entry.get(attributeName);
        if (attribute != null && attribute.isHumanReadable()) {
            List<String> values = new ArrayList<>();
            attribute.iterator().forEachRemaining(value -> values.add(value.toString()));
            setter.accept(values);
        }
    }

    public abstract String getDnBase();

    public abstract String getObjectFilter();

    public abstract SearchScope getSearchScope();

    public abstract T getNullEntry();


}