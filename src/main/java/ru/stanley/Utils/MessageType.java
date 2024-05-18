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
    CHAT_MESSAGE(
            "{\"type\":\"CHAT_MESSAGE\",\"data\":{\"sender\":\"\",\"recipient\":\"\",\"message\":\"\"}}"
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
