package com.mgamboa.handler;

import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class GameWSHandler implements WebSocketHandler {

    private final Map<InetSocketAddress, String> clientList;

    private final String[][] board = new String[3][3];

    private final Map<String, List<int[]>> stepMap = new HashMap<>();

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
//    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";

    public GameWSHandler() {
        this.clientList = new HashMap<>();
        for (String[] row : board) {
            Arrays.fill(row, "?");
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        print(GREEN,"User (%s) connected at %s%n"
                , identify(session) != null? identify(session) : session.getRemoteAddress(),
                new Date());
        if(clientList.containsKey(session.getRemoteAddress()))
            session.sendMessage(new TextMessage(String.format("Welcome back %s!", identify(session))));
        else
            askIdentity(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        String response = String.valueOf(message.getPayload());

        print(GREEN,"%s: %s%n"
                , identify(session) != null? identify(session) : session.getRemoteAddress(),
                response);

        if(response.startsWith("MOVE=")) {
            if(move(response)){
                System.out.println("WINNER");
            }
            session.sendMessage(new TextMessage(display()));
        }

        System.out.println(display());

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
        return clientList.get(session.getRemoteAddress());
    }

    private void print(String color, String format, Object... args) {
        System.out.print(color);
        System.out.printf(format, args);
        System.out.print(RESET);
    }

    private String display(){
        StringBuilder disp = new StringBuilder();
        for (String[] row : this.board) {
            disp.append(String.join("-", row));
            disp.append("\n");
        }
        return disp.toString();
    }

    private boolean move(String input){
        input = input.substring(input.indexOf("=")+1);
        String[] inputStep = input.split(":");
        String player = inputStep[1];
        String[] move = inputStep[0].split("-");
        int x = Integer.parseInt(move[0]);
        int y = Integer.parseInt(move[1]);
        board[x][y] = player;
        stepMap.computeIfAbsent(player, k -> new ArrayList<>()).add(new int[] {x, y});
        stepMap.get(player).forEach(p -> System.out.println(player + "--" +Arrays.toString(p)));

        return stepMap.get(player).contains("2,0");
    }
}

