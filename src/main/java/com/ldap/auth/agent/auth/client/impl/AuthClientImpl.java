package com.ldap.auth.agent.auth.client.impl;

import com.ldap.auth.agent.AppConfig;
import com.ldap.auth.agent.auth.client.AuthClient;
import com.ldap.auth.agent.domain.Group;
import com.ldap.auth.agent.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.json.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.Consumer;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

/**
 * @author Yuriy Tumakha
 */
@Component
public class AuthClientImpl implements AuthClient {

    private static final Logger LOG = LoggerFactory.getLogger(AuthClientImpl.class);

    // Auth API path list
    private static final String USERS = "users";
    private static final String GROUPS = "clients";

    @Autowired
    private AppConfig appConfig;

    @Override
    public List<User> getUsers() {
        LOG.info("Get Auth Users");
        List<JsonObject> list = getJsonObjects(USERS);
        List<User> users = new ArrayList<>();
        for (JsonObject json : list) {
            User user = convertJson2User(json);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<Group> getGroups() {
        LOG.info("Get Auth Groups");
        List<JsonObject> list = getJsonObjects(GROUPS);
        List<Group> groups = new ArrayList<>();
        for (JsonObject json : list) {
            Group group = convertJson2Group(json);
            if (group != null && group.getName() != null) {
                groups.add(group);
            }
        }
        return groups;
    }

    @Override
    public void createUser(User user) {
        LOG.info("Create user {}", user);

        JsonObject userJson = convertUser2Json(user, true);
        LOG.debug("Create user {}", userJson);

        Response response = postJson(USERS, userJson);
        checkResponse("Create user", response, 201);
    }

    @Override
    public void updateUser(User user) {
        LOG.info("Update user {}", user);

        String userId = user.getAuthId();
        JsonObject userJson = convertUser2Json(user, false);
        LOG.debug("Update user(id={}) {}", userId, userJson);

        Response response = patchJson(USERS + "/" + userId, userJson);
        checkResponse("Update user", response, 200);
    }

    @Override
    public void deleteUser(User user) {
        LOG.info("Delete user {}", user);

        String userId = user.getAuthId();
        LOG.debug("Delete user(id={})", userId);

        Response response = delete(USERS + "/" + userId);
        checkResponse("Delete user", response, 204);
    }

    @Override
    public void createGroup(Group group) {
        LOG.info("Create group {}", group);

        JsonObject groupJson = convertGroup2Json(group);
        LOG.debug("Create group {}", groupJson);

        Response response = postJson(GROUPS, groupJson);
        checkResponse("Create group", response, 201);
    }

    @Override
    public void updateGroup(Group group) {
        LOG.info("Update group {}", group);

        String groupId = group.getAuthId();
        JsonObject groupJson = convertGroup2Json(group);
        LOG.debug("Update group(id={}) {}", groupId, groupJson);

        Response response = patchJson(GROUPS + "/" + groupId, groupJson);
        checkResponse("Update group", response, 200);
    }

    @Override
    public void deleteGroup(Group group) {
        LOG.info("Delete group {}", group);

        String groupId = group.getAuthId();
        LOG.debug("Delete group(id={})", groupId);

        Response response = delete(GROUPS + "/" + groupId);
        checkResponse("Delete group", response, 204);
    }

    private JsonObject convertUser2Json(User user, boolean isNewUser) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("connection", "Username-Password-Authentication");

        addJsonAttribute(builder, "email", user.getEmail());
        // TODO: Temporary fix for Auth0
        if (isNewUser) {
            addJsonAttribute(builder, "password", "Password13");
        }

        JsonObjectBuilder builder2 = Json.createObjectBuilder()
            .add("ldap_user_id", user.getUserId());
        addJsonAttribute(builder2, "country_code", user.getCountryCode());
        addJsonAttribute(builder2, "username", user.getUsername());
        addJsonAttribute(builder2, "password", user.getPassword());
        addJsonAttribute(builder2, "first_name", user.getFirstName());
        addJsonAttribute(builder2, "last_name", user.getLastName());
        addJsonAttribute(builder2, "phone", user.getPhone());
        if (user.getMembership() != null && user.getMembership().size() > 0) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (String group : user.getMembership()) {
                arrayBuilder.add(group);
            }
            builder2.add("membership", arrayBuilder);
        }
        return builder.add("user_metadata", builder2).build();
    }

    private void addJsonAttribute(JsonObjectBuilder builder, String attributeName, String value) {
        if (!StringUtils.isEmpty(value)) {
            builder.add(attributeName, value);
        }
    }

    private User convertJson2User(JsonObject json) {
        User user = new User();
        user.setUserId("");
        setStringProperty(user::setAuthId, json.getJsonString("user_id"));
        setStringProperty(user::setEmail, json.getJsonString("email"));

        JsonObject userMetadata = json.getJsonObject("user_metadata");
        if (userMetadata != null) {
            setStringProperty(user::setUserId, userMetadata.getJsonString("ldap_user_id"));
            setStringProperty(user::setUsername, userMetadata.getJsonString("username"));
            setStringProperty(user::setPassword, userMetadata.getJsonString("password"));
            setStringProperty(user::setCountryCode, userMetadata.getJsonString("country_code"));
            setStringProperty(user::setFirstName, userMetadata.getJsonString("first_name"));
            setStringProperty(user::setLastName, userMetadata.getJsonString("last_name"));
            setStringProperty(user::setPhone, userMetadata.getJsonString("phone"));
            JsonArray membership = userMetadata.getJsonArray("membership");
            if (membership != null) {
                List<String> groups = new ArrayList<>();
                for (JsonString group : membership.getValuesAs(JsonString.class)) {
                    if (group != null) {
                        groups.add(group.getString());
                    }
                }
                user.setMembership(groups);
            }
        }
        return user;
    }

    private void setStringProperty(Consumer<String> setter, JsonString jsonString) {
        if (jsonString != null) {
            setter.accept(jsonString.getString());
        }
    }

    private JsonObject convertGroup2Json(Group group) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        addJsonAttribute(builder, "name", group.getName());
        return builder.build();
    }

    private Group convertJson2Group(JsonObject json) {
        Group group = new Group();
        setStringProperty(group::setName, json.getJsonString("name"));

        boolean global = json.getBoolean("global");
        if (global || "All Applications".equals(group.getName())) {
            return null;
        }
        setStringProperty(group::setAuthId, json.getJsonString("client_id"));
        return group;
    }


    // REST helpers

    private List<JsonObject> getJsonObjects(String path) {
        return getInvocationBuilder(path).get(JsonArray.class).getValuesAs(JsonObject.class);
    }

    private Response postJson(String path, JsonObject json) {
        return getInvocationBuilder(path).header("Content-Type", "application/json; charset=utf-8")
                .post(Entity.json(json.toString()));
    }

    private Response patchJson(String path, JsonObject json) {
        return getInvocationBuilder(path).header("Content-Type", "application/json; charset=utf-8")
                .method("PATCH", Entity.json(json));
    }

    private Response delete(String path) {
        return getInvocationBuilder(path).delete();
    }

    private Invocation.Builder getInvocationBuilder(String path) {
        return appConfig.getAuthApiWebTarget().path(path).request(APPLICATION_JSON_TYPE)
            .header("Authorization", appConfig.getAuthApiHeader());
    }

    private boolean checkResponse(String operation, Response response, int expectedCode) {
        boolean success = response.getStatus() == 200 || response.getStatus() == expectedCode;
        if (!success) {
            LOG.error(String.format("%s is failed. Response code %s. Headers: %s\n%s", operation,
                    response.getStatus(), response.getHeaders(), response.readEntity(String.class)));
        }
        return success;
    }

}