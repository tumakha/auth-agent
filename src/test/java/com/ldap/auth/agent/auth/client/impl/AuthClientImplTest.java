package com.ldap.auth.agent.auth.client.impl;

import com.ldap.auth.agent.AppConfig;
import com.ldap.auth.agent.auth.client.AuthClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Yuriy Tumakha
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, AuthClientImpl.class})
@TestPropertySource("classpath:application-test.properties")
public class AuthClientImplTest {

    @Autowired
    private AuthClient authClient;

    @Test
    public void testGroups() {
        authClient.getGroups();
    }

    @Test
    public void testUsers() {
        authClient.getUsers();
    }

}