package ru.stanley.Manager;

import ru.stanley.Hadlers.ClientHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void addClient(String userId, ClientHandler clientHandler) {
        clients.put(userId, clientHandler);
    }

    public static void removeClient(String userId) {
        clients.remove(userId);
    }

    public static ClientHandler getClient(String userId) {
        return clients.get(userId);
    }
}
