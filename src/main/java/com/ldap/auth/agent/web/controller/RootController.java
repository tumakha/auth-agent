package com.ldap.auth.agent.web.controller;

import com.ldap.auth.agent.auth.service.GroupService;
import com.ldap.auth.agent.auth.service.UserService;
import com.ldap.auth.agent.domain.Group;
import com.ldap.auth.agent.domain.LdapEntry;
import com.ldap.auth.agent.domain.User;
import com.ldap.auth.agent.ldap.LdapReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Yuriy Tumakha
 */
@Controller
@RequestMapping("/")
public class RootController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private LdapReader ldapReader;

    @RequestMapping(method = RequestMethod.GET)
    public RedirectView get() {
        return new RedirectView("index.jsp");
    }

    @RequestMapping(value = "/ldap", method = RequestMethod.GET, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, List<? extends LdapEntry>>> ldapData() {
        return new ResponseEntity<>(getResponse(ldapReader.getUsers(), ldapReader.getGroups()), HttpStatus.OK);
    }

    @RequestMapping(value = "/auth", method = RequestMethod.GET, produces = {APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, List<? extends LdapEntry>>> authData() {
        return new ResponseEntity<>(getResponse(userService.getAll(), groupService.getAll()), HttpStatus.OK);
    }

    private Map<String, List<? extends LdapEntry>> getResponse(List<User> users, List<Group> groups) {
        Map<String, List<? extends LdapEntry>> map = new TreeMap<>();
        map.put("users", users);
        map.put("groups", groups);
        return map;
    }

}