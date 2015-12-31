package com.ldap.auth.agent.ldap.mapper;

import com.ldap.auth.agent.domain.GeneralEntry;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidAttributeValueException;
import org.apache.directory.ldap.client.template.EntryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuriy Tumakha
 */
public class GeneralMapper implements EntryMapper<GeneralEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(GeneralMapper.class);

    @Override
    public GeneralEntry map(Entry entry) throws LdapException {
        GeneralEntry generalEntry = new GeneralEntry();
        generalEntry.setDn(entry.getDn().getName());
        Map<String, String> attributes = new HashMap<>();
        for (Attribute attribute : entry.getAttributes()) {
            try {
                String value = attribute.isHumanReadable() ?
                        attribute.getString() : new String(attribute.getBytes(), StandardCharsets.UTF_8);
                attributes.put(attribute.getId(), value);
            } catch (LdapInvalidAttributeValueException e) {
                LOG.error(String.format("Can't read attribute %s. %s", attribute.getId(), e.getMessage()), e);
            }
        }
        generalEntry.setAttributes(attributes);
        return generalEntry;
    }

}