package com.ldap.auth.agent.task.impl;

import com.ldap.auth.agent.AppConfig;
import com.ldap.auth.agent.auth.service.impl.GroupServiceImpl;
import com.ldap.auth.agent.auth.service.impl.UserServiceImpl;
import com.ldap.auth.agent.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ldap.auth.agent.domain.LdapEntryComparator.COMPARATOR;
import static org.junit.Assert.assertEquals;

/**
 * @author Yuriy Tumakha
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class, SyncTaskImpl.class,
        UserServiceImpl.class, GroupServiceImpl.class, LdapReaderMock.class, AuthClientMock.class})
@TestPropertySource("classpath:application-test.properties")
public class SyncTaskImplTest {

    @Autowired
    private LdapReaderMock ldapReader;

    @Autowired
    private AuthClientMock authClient;

    @Autowired
    private SyncTaskImpl syncTask;

    @Before
    public void init() throws Exception {
        syncTask.init();
        List<User> ldapUsers = IntStream.rangeClosed(1, 100).mapToObj(i -> buildUser(i)).collect(Collectors.toList());
        ldapReader.setUsers(ldapUsers);
    }

    @Test
    public void testEmptyAuthList() {
        authClient.setUsers(new ArrayList<>());
        syncTask.sync();
        verifyUserLists();
    }

    @Test
    public void testAuthListHalf1OfLDAP() {
        authClient.setUsers(IntStream.rangeClosed(1, 50).mapToObj(i -> buildUser(i)).collect(Collectors.toList()));
        syncTask.sync();
        verifyUserLists();
    }

    @Test
    public void testAuthListHalf2OfLDAP() {
        authClient.setUsers(IntStream.rangeClosed(51, 100).mapToObj(i -> buildUser(i)).collect(Collectors.toList()));
        syncTask.sync();
        verifyUserLists();
    }

    @Test
    public void testAuthListEqualsLDAP() {
        authClient.setUsers(IntStream.rangeClosed(1, 100).mapToObj(i -> buildUser(i)).collect(Collectors.toList()));
        syncTask.sync();
        verifyUserLists();

        // test already synchronized (previousHash2 != null)
        syncTask.sync();
        verifyUserLists();
    }

    @Test
    public void testAuthListBiggerThanLDAP() {
        authClient.setUsers(IntStream.rangeClosed(50, 200).mapToObj(i -> buildUser(i)).collect(Collectors.toList()));
        syncTask.sync();
        verifyUserLists();
    }

    @Test
    public void testAuthListFirstToDelete() {
        authClient.setUsers(IntStream.rangeClosed(0, 10).mapToObj(i -> buildUser(i)).collect(Collectors.toList()));
        syncTask.sync();
        verifyUserLists();
    }

    @Test
    public void testUpdateUser() {
        List<User> authUsers = IntStream.rangeClosed(1, 20).mapToObj(i -> buildUser(i)).collect(Collectors.toList());
        authUsers.get(12).setLastName("LastName2update");
        authClient.setUsers(authUsers);
        syncTask.sync();
        verifyUserLists();
    }

    @Test
    public void testBothEmptyLists() {
        ldapReader.setUsers(new ArrayList<>());
        authClient.setUsers(new ArrayList<>());
        syncTask.sync();

        List<User> ldapUsers = ldapReader.getUsers();
        List<User> authUsers = authClient.getUsers();
        assertEquals(ldapUsers.size(), authUsers.size());
        assertEquals(authUsers.size(), 0);
        assertEquals(ldapUsers.hashCode(), authUsers.hashCode());
    }

    @Test
    public void testBigUserLists() {
        List<User> ldapUsers =
                IntStream.iterate(1, i -> i + 2).limit(9000).mapToObj(i -> buildUser(i)).collect(Collectors.toList());
        ldapReader.setUsers(ldapUsers);

        List<User> authUsers =
                IntStream.iterate(1, i -> i + 3).limit(8000).mapToObj(i -> buildUser(i)).collect(Collectors.toList());
        authClient.setUsers(authUsers);
        syncTask.sync();

        ldapUsers = ldapReader.getUsers();
        authUsers = authClient.getUsers();
        Collections.sort(ldapUsers, COMPARATOR);
        Collections.sort(authUsers, COMPARATOR);
        assertEquals(ldapUsers.size(), authUsers.size());
        assertEquals(authUsers.size(), 9000);
        assertEquals(ldapUsers.hashCode(), authUsers.hashCode());
    }

    private void verifyUserLists() {
        List<User> ldapUsers = ldapReader.getUsers();
        List<User> authUsers = authClient.getUsers();
        Collections.sort(authUsers, COMPARATOR);
        assertEquals(ldapUsers.size(), authUsers.size());
        assertEquals(authUsers.size(), 100);
        assertEquals(ldapUsers.hashCode(), authUsers.hashCode());
        assertEquals(ldapUsers.get(0).getUserId(), authUsers.get(0).getUserId());
        assertEquals(ldapUsers.get(1).getPassword(), authUsers.get(1).getPassword());
        assertEquals(ldapUsers.get(12).getLastName(), authUsers.get(12).getLastName());
        assertEquals("Lastname13", authUsers.get(12).getLastName());

        assertEquals(0, ldapReader.getGroups().size());
        assertEquals(0, authClient.getGroups().size());
    }

    private User buildUser(int i) {
        User user = new User();
        user.setUserId(String.format("test.user%03d@example.com", i));
        user.setPassword("Password" + i);
        user.setFirstName("Firstname");
        user.setLastName("Lastname" + i);
        return user;
    }

}