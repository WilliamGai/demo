package com.sonic.website.app.websocket;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.sonic.website.core.common.support.LogCore;
import com.sonic.website.core.common.support.Util;


@ServerEndpoint(value = "/ws")
// @Component
/**
 * 自定义规则，每次前端链接过来，主动发送openid
 */
public class WebSocket {
    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss:SSS");// 显示日期格式

    public static AtomicInteger ONLINE_COUNT = new AtomicInteger();

    private Session session;
    private long begin_time;
    /** k,v : openid, sessionid */
    public static Map<String, WebSocket> OPENID_MAP = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        begin_time = System.currentTimeMillis();
        this.session = session;
        LogCore.BASE.debug("new session joined, sessionid:{},conn count:{}", session.getId(), getOnlineCount());
    }

    @OnClose
    public void onClose() {
        long interval_time = System.currentTimeMillis() - begin_time;
        LogCore.BASE.debug("one session closed ,sessionId is{}, left conn num is{}, interval_time :{}",
                this.session.getId(), getOnlineCount(), interval_time);
        rmvOpenid(this);// TT
        subOnlineCount();
    }
    /*不处理*/
    @OnError
    public void onError(Throwable t, Session session) {
        LogCore.BASE.debug("webscoket OnError:sessionid={}", session.getId());
    }

    /**
     * 收到消息,前端会在自己的websocket链接打开后发送openid
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        LogCore.BASE.debug("rsvd from client:{},sessionid:{}", message, session.getId());
        if (Util.isEmpty(message)) {
            return;
        }
        addOnlineCount();
        OPENID_MAP.put(message, this);
        String s ="";
        this.sendMessage(s);
    }

    public static void rmvOpenid(WebSocket ws) {
        LogCore.BASE.debug("before session closed ,sessionId is{}, left conn num is{}", ws.session.getId(),
                OPENID_MAP.size());
        for (Iterator<Map.Entry<String, WebSocket>> it = OPENID_MAP.entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, WebSocket> entry = it.next();
            if (entry.getValue().equals(ws)) {
                it.remove();
                break;//
            }
        }
        LogCore.BASE.debug("after session closed ,sessionId is{}, left conn num is{}", ws.session.getId(),
                OPENID_MAP.size());
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LogCore.BASE.warn("websocket send msg err:message:{}", message, e);
        }
    }

    /**
     * 
     * @param openid
     */
    public static void sendMessage(String openid, String msg) {
        if (OPENID_MAP.containsKey(openid)) {
            OPENID_MAP.get(openid).sendMessage(msg);
        }
    }

    /** 通知所有的websocket连接 */
    public static void send2AllSocket(String msg) {
        OPENID_MAP.values().forEach(x -> {
            x.sendMessage(msg);
//            LogCore.BASE.debug("websocket tell {} ,{}",x.session.getId(), msg);
        });

    }

    public static int getOnlineCount() {
        return ONLINE_COUNT.get();
    }

    public static int addOnlineCount() {
        return ONLINE_COUNT.incrementAndGet();
    }

    public static int subOnlineCount() {
        return ONLINE_COUNT.decrementAndGet();
    }
}