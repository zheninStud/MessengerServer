package ru.stanley.Utils;

import org.json.JSONObject;

public enum MessageType {

    AUTH(
            "{\"type\":\"AUTH\",\"data\":{\"username\":\"\",\"password\":\"\"}}"
    ),
    REGISTER(
            "{\"type\":\"REGISTER\",\"data\":{\"username\":\"\",\"password\":\"\",\"email\":\"\",\"phone\":\"\"}}"
    ),
    REGISTER_SUCCESS(
        "{\"type\":\"REGISTER_SUCCESS\"}"
    ),
    REGISTER_FAIL(
        "{\"type\":\"REGISTER_FAIL\"}"
    ),
    CHAT_MESSAGE(
            "{\"type\":\"CHAT_MESSAGE\",\"data\":{\"sender\":\"\",\"recipient\":\"\",\"message\":\"\"}}"
    );

    private final String jsonTemplate;

    MessageType(String jsonTemplate) {
        this.jsonTemplate = jsonTemplate;
    }

    public String getJsonTemplate() {
        return jsonTemplate;
    }

    // Метод для создания JSON объекта из шаблона
    public JSONObject createJsonObject() {
        return new JSONObject(jsonTemplate);
    }

}
