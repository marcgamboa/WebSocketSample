package com.mgamboa.handler;

import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class GameWebSocketHandler implements WebSocketHandler {

    private final Map<InetSocketAddress, String> clientList;

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";

    public GameWebSocketHandler() {
        this.clientList = new HashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        print(GREEN,"User (%s) connected at %s%n"
                , identify(session) != null? identify(session) : session.getLocalAddress(),
                new Date());
        if(clientList.containsKey(session.getLocalAddress()))
            session.sendMessage(new TextMessage(String.format("Welcome back %s!", identify(session))));
        else
            askIdentity(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        String response = String.valueOf(message.getPayload());

        print(GREEN,"%s: %s%n"
                , identify(session) != null? identify(session) : session.getLocalAddress(),
                response);


        if(response.startsWith("ANSWER::"))
            clientList.put(session.getLocalAddress(), response.substring(response.indexOf("::") + 2));
        else if(response.equalsIgnoreCase("ping"))
            session.sendMessage(new TextMessage("pong"));
        else
            session.sendMessage(new TextMessage(response));

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        print(YELLOW,"Error from user (%s) - %s%n", identify(session), exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        print(YELLOW,"User (%s) disconnected at %s%n", identify(session), new Date());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void askIdentity(WebSocketSession session) throws IOException {
        session.sendMessage(new TextMessage("QUESTION::Hello new user, can you please identify yourself?"));
    }

    private String identify(WebSocketSession session) {
        return clientList.get(session.getLocalAddress());
    }

    private void print(String color, String format, Object... args) {
        System.out.print(color);
        System.out.printf(format, args);
        System.out.print(RESET);
    }
}

