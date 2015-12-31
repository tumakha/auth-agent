package com.ldap.auth.agent.ldap;

import com.ldap.auth.agent.AppConfig;
import com.ldap.auth.agent.domain.User;
import com.ldap.auth.agent.ldap.impl.LdapReaderImpl;
import com.ldap.auth.agent.ldap.mapper.GroupMapper;
import com.ldap.auth.agent.ldap.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Yuriy Tumakha
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, LdapReaderImpl.class, UserMapper.class, GroupMapper.class})
@TestPropertySource("classpath:application-test.properties")
public class LdapReaderTest {

    private static final Logger LOG = LoggerFactory.getLogger(LdapReaderTest.class);

    @Autowired
    private LdapReader ldapReader;

    @Test
    public void testGetUsers() {
        List<User> ldapEntries = ldapReader.getUsers();
        LOG.debug("LDAP Users:\n" + ldapEntries);
        assertTrue(ldapEntries.size() > 0);
    }

}