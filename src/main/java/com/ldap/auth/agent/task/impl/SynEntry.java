package com.ldap.auth.agent.task.impl;

import com.ldap.auth.agent.auth.service.AuthService;
import com.ldap.auth.agent.domain.LdapEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

/**
 * @author Yuriy Tumakha
 */
public class SynEntry<E extends LdapEntry> {

    private static final Logger LOG = LoggerFactory.getLogger(SynEntry.class);

    private List<E> entries1;
    private AuthService<E> authService;
    private Map<String, Integer> hash2Cash;
    private boolean ignoreAuthListCash = true;

    public SynEntry(List<E> entries1, AuthService<E> authService, Map<String, Integer> hash2Cash) {
        this.entries1 = entries1;
        this.authService = authService;
        this.hash2Cash = hash2Cash;
    }
    
    public void sync() {
        LOG.debug("Checking hashes of entry lists for synchronization");

        int hash1 = entries1.hashCode();
        String genericName = getGenericName();
        if (!ignoreAuthListCash) {
            Integer previousHash2 = hash2Cash.get(genericName);
            if (previousHash2 != null && compareListHash(hash1, previousHash2)) {
                return;
            }
        }
        List<E> entries2 = authService.getAll();
        int hash2 = entries2.hashCode();
        hash2Cash.put(genericName, hash2);
        if (compareListHash(hash1, hash2)) {
            return;
        }

        LOG.info("Start {} synchronization", genericName);
        int i = 0, j = 0;
        while (i < entries1.size() || j < entries2.size()) {
            E entry1 = null, entry2 = null;
            if (i < entries1.size()) {
                entry1 = entries1.get(i);
            } else {
                for (int k = j; k < entries2.size(); k++) {
                    delete(entries2.get(k));
                }
                return;
            }
            if (j < entries2.size()) {
                entry2 = entries2.get(j);
            } else {
                for (int k = i; k < entries1.size(); k++) {
                    create(entries1.get(k));
                }
                return;
            }
            String id1 = entry1.getId();
            String id2 = entry2.getId();
            if (id1.equals(id2)) {
                if (entry1.hashCode() != entry2.hashCode()) {
                    entry1.setAuthId(entry2.getAuthId());
                    update(entry1);
                }
                i++;
                j++;
            } else if (id1.compareTo(id2) < 0) {
                create(entry1);
                i++;
            } else {
                delete(entry2);
                j++;
            }
        }

    }

    private boolean compareListHash(int ldapHash, int authHash) {
        boolean equals = ldapHash == authHash;
        if (equals) {
            LOG.debug("Auth {} list are up to date. Sync with LDAP is canceled.", getGenericName());
        }
        return equals;
    }

    private String getGenericName() {
        ParameterizedType parameterizedType = (ParameterizedType) authService.getClass().getGenericSuperclass();
        Class clazz = (Class) parameterizedType.getActualTypeArguments()[0];
        return clazz.getSimpleName();
    }

    private void create(E entry) {
        LOG.debug("Create {}", entry);
        authService.create(entry);
    }

    private void update(E entry) {
        LOG.debug("Update {}", entry);
        authService.update(entry);
    }

    private void delete(E entry) {
        LOG.debug("Delete {}", entry);
        authService.delete(entry);
    }

}