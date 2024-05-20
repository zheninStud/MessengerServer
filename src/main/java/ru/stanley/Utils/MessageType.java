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
    GET_SALT(
            "{\"type\":\"GET_SALT\",\"data\":{\"username\":\"\"}}"
    ),
    SET_SALT(
            "{\"type\":\"SET_SALT\",\"data\":{\"salt\":\"\"}}"
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
    CHAT_MESSAGE(
            "{\"type\":\"CHAT_MESSAGE\",\"data\":{\"sender\":\"\",\"recipient\":\"\",\"message\":\"\"}}"
    ),
    GET_USER(
            "{\"type\":\"GET_USER\",\"data\":{\"username\":\"\"}}"
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
