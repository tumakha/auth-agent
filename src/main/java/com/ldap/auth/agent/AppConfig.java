package com.ldap.auth.agent;

import com.ldap.auth.agent.ldap.mapper.GroupMappings;
import com.ldap.auth.agent.ldap.mapper.UserMappings;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.directory.ldap.client.api.DefaultPoolableLdapConnectionFactory;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.template.LdapConnectionTemplate;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * @author Yuriy Tumakha
 */
@Configuration

public class AppConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Autowired
    private Environment env;

    private String webUser;

    private String webPassword;

    private String syncInterval;

    private String authApiHeader;

    private WebTarget authApiWebTarget;

    private LdapConnectionTemplate ldapConnectionTemplate;

    private final UserMappings userMappings = new UserMappings();

    private final GroupMappings groupMappings = new GroupMappings();

    @PostConstruct
    public void init() {
        webUser =  env.getRequiredProperty("auth.agent.web.admin");
        webPassword = env.getRequiredProperty("auth.agent.web.password");

        syncInterval = env.getProperty("sync.interval", "5 sec");

        String authApiBase = env.getRequiredProperty("auth.api.base");
        String authApiJWT = env.getRequiredProperty("auth.api.jwt");
        authApiHeader = "Bearer " + authApiJWT;
        Client client = ClientBuilder.newClient();
        authApiWebTarget = client.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true)
            .register(JsonProcessingFeature.class).target(authApiBase);

        ldapConnectionTemplate = new LdapConnectionTemplate(getLdapConnectionPool());

        String userPrefix = "ldap.user";
        userMappings.setUsersDN(getProperty(userPrefix, "dn.base"));
        userMappings.setUsersObjectFilter(getProperty(userPrefix, "object.filter"));
        userMappings.setSearchScope(getProperty(userPrefix, "search.scope"));
        userMappings.setUserId(getProperty(userPrefix, "id"));
        userMappings.setEmail(getProperty(userPrefix, "email"));
        userMappings.setUsername(getProperty(userPrefix, "username"));
        userMappings.setPassword(getProperty(userPrefix, "password"));
        userMappings.setCountryCode(getProperty(userPrefix, "countrycode"));
        userMappings.setFirstName(getProperty(userPrefix, "firstname"));
        userMappings.setLastName(getProperty(userPrefix, "lastname"));
        userMappings.setPhone(getProperty(userPrefix, "phone"));
        userMappings.setMemberOf(getProperty(userPrefix, "memberof"));
        userMappings.setSyncGroupsDN(getProperty(userPrefix, "sync.groups.dn"));

        String groupPrefix = "ldap.group";
        groupMappings.setGroupsDN(getProperty(groupPrefix, "dn.base"));
        groupMappings.setGroupsObjectFilter(getProperty(groupPrefix, "object.filter"));
        groupMappings.setSearchScope(getProperty(groupPrefix, "search.scope"));
        groupMappings.setName(getProperty(groupPrefix, "name"));
        groupMappings.setDescription(getProperty(groupPrefix, "description"));
        groupMappings.setMember(getProperty(groupPrefix, "member"));
    }

    @Bean
    public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
        return container -> container.setPort(env.getProperty("auth.agent.web.port", Integer.class));
    }

    public String getWebUser() {
        return webUser;
    }

    public String getWebPassword() {
        return webPassword;
    }

    public String getSyncInterval() {
        return syncInterval;
    }

    public String getAuthApiHeader() {
        return authApiHeader;
    }

    public WebTarget getAuthApiWebTarget() {
        return authApiWebTarget;
    }

    public LdapConnectionTemplate getLdapConnectionTemplate() {
        return ldapConnectionTemplate;
    }

    public UserMappings getUserMappings() {
        return userMappings;
    }

    public GroupMappings getGroupMappings() {
        return groupMappings;
    }

    private String getProperty(String prefix, String key) {
        String property = new StringBuilder(prefix).append(".").append(key).toString();
        return env.getProperty(property);
    }

    private LdapConnectionPool getLdapConnectionPool() {
        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost(env.getRequiredProperty("ldap.host"));
        config.setLdapPort(env.getRequiredProperty("ldap.port", Integer.class));
        config.setName(env.getRequiredProperty("ldap.admin.dn"));
        config.setCredentials(env.getRequiredProperty("ldap.admin.password"));

        GenericObjectPool.Config poolConfig = new GenericObjectPool.Config();
        poolConfig.testOnBorrow = true;

        return new LdapConnectionPool(new DefaultPoolableLdapConnectionFactory(config), poolConfig);
    }

}