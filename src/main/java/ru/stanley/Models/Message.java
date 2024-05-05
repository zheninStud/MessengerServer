package ru.stanley.Models;

import org.json.JSONObject;

public class Message {

    private String type;
    private JSONObject data;

    public Message(String type, JSONObject data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public JSONObject getData() {
        return data;
    }

    public static Message fromJSON(String jsonStr) {
        JSONObject jsonObj = new JSONObject(jsonStr);
        String type = jsonObj.getString("type");
        JSONObject data = jsonObj.getJSONObject("data");
        return new Message(type, data);
    }

    public String toJSON() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("type", type);
        jsonObj.put("data", data);
        return jsonObj.toString();
    }

}
