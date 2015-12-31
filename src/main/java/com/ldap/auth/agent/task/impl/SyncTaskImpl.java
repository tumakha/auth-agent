package com.ldap.auth.agent.task.impl;

import com.ldap.auth.agent.auth.service.GroupService;
import com.ldap.auth.agent.auth.service.UserService;
import com.ldap.auth.agent.ldap.LdapReader;
import com.ldap.auth.agent.task.SyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author Yuriy Tumakha
 */
@Service("syncTask")
public class SyncTaskImpl implements SyncTask {

    private static final Logger LOG = LoggerFactory.getLogger(SyncTaskImpl.class);

    @Autowired
    private LdapReader ldapReader;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    private final Map<String, Integer> hash2Cash = new HashMap<>();

    @PostConstruct
    public void init() {
        hash2Cash.clear();
    }

    @Override
    public void sync() {
        new SynEntry<>(ldapReader.getUsers(), userService, hash2Cash).sync();
        new SynEntry<>(ldapReader.getGroups(), groupService, hash2Cash).sync();
    }

}