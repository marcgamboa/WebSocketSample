package com.mgamboa.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.mockito.Mockito.*;

public class GameWSHandlerTest {

    private GameWSHandler gameWSHandler;
    private WebSocketSession session;

    @BeforeEach
    void setup(){
        gameWSHandler = new GameWSHandler();
        session = mock(WebSocketSession.class);
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress("192.168.1.1", 443));
    }

    @Test
    void afterConnectionEstablished() throws Exception {
        gameWSHandler.afterConnectionEstablished(session);
        verify(session, times(3)).getRemoteAddress();
    }

    @Test
    void handleMessage() throws IOException {
        TextMessage msg1 = new TextMessage("MOVE=2-2:X");
        TextMessage msg2 = new TextMessage("MOVE=1-2:O");
        TextMessage msg3 = new TextMessage("MOVE=1-1:X");
        TextMessage msg4 = new TextMessage("MOVE=2-0:O");

        gameWSHandler.handleMessage(session, msg1);
        gameWSHandler.handleMessage(session, msg2);
        gameWSHandler.handleMessage(session, msg3);
        gameWSHandler.handleMessage(session, msg4);

        verify(session, times(8)).getRemoteAddress();
    }

    @Test
    void handleTransportError() {
        gameWSHandler.handleTransportError(session, new Throwable("TEST"));
        verify(session, times(1)).getRemoteAddress();
    }

    @Test
    void afterConnectionClosed() {
        gameWSHandler.afterConnectionClosed(session, CloseStatus.NORMAL);
        verify(session, times(1)).getRemoteAddress();

    }
}