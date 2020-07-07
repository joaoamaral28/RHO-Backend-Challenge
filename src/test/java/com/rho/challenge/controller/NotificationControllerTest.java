package com.rho.challenge.controller;

import com.rho.challenge.ChallengeApplication;
import com.rho.challenge.model.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ChallengeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
class NotificationControllerTest {

    /* Aux class to debug websocket processed frames
    * src: https://stackoverflow.com/questions/51382606/spring-websocket-integration-test-works-at-random */
    class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return byte[].class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            System.out.println("=============================================================");
            System.out.println(new String((byte[]) o));
            System.out.println("=============================================================");
            blockingQueue.offer(new String((byte[]) o));
        }
    }

    private int port = 8080;

    private String WS_URI = "ws://localhost:" + port;

    private String WS_TOPIC_MAIN = "/gs-guide-websocket";

    private String WS_TOPIC_SUB_MAIN = "/topic";
    private String WS_TOPIC_SUB_BET = "/notifications";
    private String WS_TOPIC_SND_MAIN = "/monitor";
    private String WS_TOPIC_SND_BET = "/process_bet";

    BlockingQueue<String> blockingQueue;
    WebSocketStompClient stompClient;

    /*
    stompClient.subscribe('/topic/notifications',
    stompClient.send("/monitor/process_bet"
     */

    @BeforeEach
    public void setup(){
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(Arrays.asList(new WebSocketTransport((new StandardWebSocketClient())))));
    }

    @Test
    public void testReceiveBetConfirmationFromServer() throws InterruptedException, ExecutionException, TimeoutException {

        StompSession session = stompClient.connect(WS_URI+WS_TOPIC_MAIN, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        session.subscribe(WS_TOPIC_SUB_MAIN + WS_TOPIC_SUB_BET, new DefaultStompFrameHandler());

        String s_bet = "{\"account_id\":\"1\",\"stake\":\"10\"}";
        session.send(WS_TOPIC_SND_MAIN + WS_TOPIC_SND_BET, s_bet.getBytes());

        assertEquals("OK", blockingQueue.poll(1, TimeUnit.SECONDS));
    }

    @Test
    void testProcessBetReturnsNotification() throws InterruptedException, ExecutionException, TimeoutException {

        StompSession session = stompClient.connect(WS_URI+WS_TOPIC_MAIN, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        session.subscribe(WS_TOPIC_SUB_MAIN + WS_TOPIC_SUB_BET, new DefaultStompFrameHandler());

        String s_bet = "{\"account_id\":\"1\",\"stake\":\"100\"}";
        session.send(WS_TOPIC_SND_MAIN + WS_TOPIC_SND_BET, s_bet.getBytes());

        Notification n_expect = new Notification(1,100.0);
        String s_expect = n_expect.toJSONString();

        assertEquals(s_expect, blockingQueue.poll(1, TimeUnit.SECONDS));

    }

    @Test
    void testProcessBetBadMessageFormat() throws InterruptedException, ExecutionException, TimeoutException{

        StompSession session = stompClient.connect(WS_URI+WS_TOPIC_MAIN, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        session.subscribe(WS_TOPIC_SUB_MAIN + WS_TOPIC_SUB_BET, new DefaultStompFrameHandler());

        String s_bet = "{\"account_id\":\"1\",\"stake\":}";
        String s_bet1 = "{\"account_id\":\"1\" \"stake\":\"2\"}";

        session.send(WS_TOPIC_SND_MAIN + WS_TOPIC_SND_BET, s_bet.getBytes());
        assertEquals("Invalid JSON format", blockingQueue.poll(1, TimeUnit.SECONDS));

        session.send(WS_TOPIC_SND_MAIN + WS_TOPIC_SND_BET, s_bet1.getBytes());
        assertEquals("Invalid JSON format", blockingQueue.poll(1, TimeUnit.SECONDS));
    }

    @Test
    void testProcessBetBadMessageElement() throws InterruptedException, ExecutionException, TimeoutException{

        StompSession session = stompClient.connect(WS_URI+WS_TOPIC_MAIN, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        session.subscribe(WS_TOPIC_SUB_MAIN + WS_TOPIC_SUB_BET, new DefaultStompFrameHandler());

        String s_bet = "{\"account_id\":\"1\",\"stake\":\"abc\"}";
        session.send(WS_TOPIC_SND_MAIN + WS_TOPIC_SND_BET, s_bet.getBytes());
        assertEquals("Bet ID and Stake amount must be digits", blockingQueue.poll(1, TimeUnit.SECONDS));

        String s_bet1 = "{\"account_id\":\"@@\", \"stake\":\"2\"}";
        session.send(WS_TOPIC_SND_MAIN + WS_TOPIC_SND_BET, s_bet1.getBytes());
        assertEquals("Bet ID and Stake amount must be digits", blockingQueue.poll(1, TimeUnit.SECONDS));

    }

    @Test
    void testProcessBetNullMessage() throws InterruptedException, ExecutionException, TimeoutException{

        StompSession session = stompClient.connect(WS_URI+WS_TOPIC_MAIN, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);
        session.subscribe(WS_TOPIC_SUB_MAIN + WS_TOPIC_SUB_BET, new DefaultStompFrameHandler());

        String s_bet = "{\"account_id\":\"1\",\"stake\":\"abc\"}";
        session.send(WS_TOPIC_SND_MAIN + WS_TOPIC_SND_BET, null);

        // assertEquals("Bet ID and Stake amount must be digits", blockingQueue.poll(1, TimeUnit.SECONDS));

    }


}