package com.ldap.auth.agent.auth.service.impl;

import com.ldap.auth.agent.auth.client.AuthClient;
import com.ldap.auth.agent.auth.service.UserService;
import com.ldap.auth.agent.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yuriy Tumakha
 */
@Service
public class UserServiceImpl extends AuthServiceImpl<User> implements UserService {

    @Autowired
    private AuthClient authClient;

    @Override
    public List<User> getAll() {
        return sort(authClient.getUsers());
    }

    @Override
    public void create(User user) {
        authClient.createUser(user);
    }

    @Override
    public void update(User user) {
        authClient.updateUser(user);
    }

    @Override
    public void delete(User user) {
        authClient.deleteUser(user);
    }

}