package ru.stanley.Utils;

import org.json.JSONObject;
import ru.stanley.Models.Message;

public enum MessageType {

    AUTH(
            "{\"type\":\"AUTH\",\"data\":{\"username\":\"\",\"passwordHash\":\"\"}}"
    ),
    REGISTER(
            "{\"type\":\"REGISTER\",\"data\":{\"username\":\"\",\"passwordHash\":\"\",\"salt\":\"\",\"email\":\"\",\"phone\":\"\"}}"
    ),
    DISCONNECT(
            "{\"type\":\"DISCONNECT\"}"
    ),
    GET_SALT(
            "{\"type\":\"GET_SALT\",\"data\":{\"username\":\"\"}}"
    ),
    SET_SALT(
            "{\"type\":\"SET_SALT\",\"data\":{\"salt\":\"\"}}"
    ),
    GET_SALT_FALSE(
            "{\"type\":\"GET_SALT_FALSE\"}"
    ),
    REGISTER_SUCCESS(
            "{\"type\":\"REGISTER_SUCCESS\"}"
    ),
    REGISTER_FAIL(
            "{\"type\":\"REGISTER_FAIL\",\"data\":{\"errorCode\":\"\"}}"
    ),
    REGUEST_FRIEND(
            "{\"type\":\"REGUEST_FRIEND\",\"data\":{\"userId\":\"\",\"publicKey\":\"\"}}"
    ),
    REGUEST_FRIEND_CLIENT(
            "{\"type\":\"REGUEST_FRIEND_CLIENT\",\"data\":{\"userId\":\"\",\"userName\":\"\",\"email\":\"\",\"phone\":\"\",\"publicKey\":\"\"}}"
    ),
    REGUEST_FRIEND_SERVER(
            "{\"type\":\"REGUEST_FRIEND_SERVER\",\"data\":{\"userId\":\"\"}}"
    ),
    REGUEST_FRIEND_CLIENT_TAKEN(
            "{\"type\":\"REGUEST_FRIEND_CLIENT_TAKEN\", \"data\":{\"userId\":\"\"}}"
    ),
    REGUEST_FRIEND_CLIENT_TAKEN_SERVER(
            "{\"type\":\"REGUEST_FRIEND_CLIENT_TAKEN_SERVER\"}"
    ),
    REGUEST_FRIEND_CLIENT_TAKEN_CLIENT(
            "{\"type\":\"REGUEST_FRIEND_CLIENT_TAKEN_CLIENT\", \"data\":{\"userId\":\"\"}}"
    ),
    REGUEST_FRIEND_CLIENT_SUCCESS(
            "{\"type\":\"REGUEST_FRIEND_CLIENT_SUCCESS\",\"data\":{\"userId\":\"\",\"publicKey\":\"\"}}"
    ),
    REGUEST_FRIEND_CLIENT_SUCCESS_CLIENT(
            "{\"type\":\"REGUEST_FRIEND_CLIENT_SUCCESS_CLIENT\",\"data\":{\"userId\":\"\",\"publicKey\":\"\"}}}"
    ),
    REGUEST_FRIEND_CLIENT_SUCCESS_SERVER(
            "{\"type\":\"REGUEST_FRIEND_CLIENT_SUCCESS_SERVER\", \"data\":{\"userId\":\"\"}}}"
    ),
    REGUEST_FRIEND_CLIENT_DENY(
            "{\"type\":\"REGUEST_FRIEND_CLIENT_DENY\", \"data\":{\"userId\":\"\"}}"
    ),
    REGUEST_FRIEND_CLIENT_DENY_CLIENT(
            "{\"type\":\"REGUEST_FRIEND_CLIENT_DENY_CLIENT\", \"data\":{\"userId\":\"\"}}"
    ),
    GET_USER(
            "{\"type\":\"GET_USER\",\"data\":{\"username\":\"\"}}"
    ),
    GET_USER_ID(
            "{\"type\":\"GET_USER_ID\",\"data\":{\"userId\":\"\"}}"
    ),
    USER_SUCCESS(
            "{\"type\":\"USER_SUCCESS\",\"data\":{\"userId\":\"\",\"userName\":\"\",\"email\":\"\",\"phone\":\"\"}}"
    ),
    USER_FAIL(
            "{\"type\":\"USER_FAIL\"}"
    ),
    DH_PUBLIC(
            "{\"type\":\"DH_PUBLIC\",\"data\":{\"userId\":\"\",\"publicKey\":\"\"}}"
    ),
    MESSAGE_SENT(
            "{\"type\":\"MESSAGE_SENT\",\"data\":{\"sender\":\"\",\"recipient\":\"\",\"message\":\"\"}}"
    ),
    MESSAGE_SENT_CLIENT(
            "{\"type\":\"MESSAGE_SENT_CLIENT\",\"data\":{\"sender\":\"\",\"recipient\":\"\",\"message\":\"\"}}"
    ),
    MESSAGE_SENT_SERVER(
            "{\"type\":\"MESSAGE_SENT_SERVER\"}"
    ),
    CHECK_MESSAGE(
            "{\"type\":\"CHECK_MESSAGE\",\"data\":{\"userId\":\"\"}}"
    ),
    AUTH_SUCCESS(
            "{\"type\":\"AUTH_SUCCESS\",\"data\":{\"userId\":\"\",\"userName\":\"\",\"email\":\"\",\"phone\":\"\"}}"
    ),
    AUTH_FAIL(
            "{\"type\":\"AUTH_FAIL\"}"
    );

    private final String jsonTemplate;

    MessageType(String jsonTemplate) {
        this.jsonTemplate = jsonTemplate;
    }

    public String getJsonTemplate() {
        return jsonTemplate;
    }

    public JSONObject createJsonObject() {
        return new JSONObject(jsonTemplate);
    }

    public Message createMessage(JSONObject data) {
        return new Message(data);
    }

}
