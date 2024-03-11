package com.example.websocket.demos.web.websocket;

import com.alibaba.fastjson.JSONObject;
import com.example.websocket.demos.web.config.Cache;
import com.example.websocket.demos.web.config.Timeline;
import com.example.websocket.demos.web.util.AliYunUtil;
import com.example.websocket.demos.web.util.BaiduAiUtil;
import com.example.websocket.demos.web.util.CallLimiter;
import com.example.websocket.demos.web.util.TigerUtil;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@ServerEndpoint("/websocket")
public class WebSocketEndpoint {
    private static CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<>();
    private Thread heartbeatThread;

    private String adUrl = "";

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("WebSocket Opened: " + session.getId());
        //startHeartbeat(session); // 开始心跳
    }

    @OnMessage
    public void onMessage(String msg, Session session) throws Exception {
        System.out.println("Message from " + session.getId() + ": " + msg);
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String ip = jsonObject.getString("ip");
        String message = jsonObject.getString("msg");
        if (!CallLimiter.allowCall(ip)) {
            session.getAsyncRemote().sendText("limit");
            return;
        }
        if (Cache.nameHash.containsKey(message) && Cache.timeLines.get(message) != null) {
            session.getAsyncRemote().sendText(message);
        } else {
            //BaiduAiUtil.sendGet(adUrl);
            LinkedList<Timeline> timeLines = new LinkedList<>();
            //timeLines.add(new Timeline("2022年","产品发布"));
            String prompt = "请根据时间线按照给定的格式列出 " + message + " 的发展历史，每行一条内容，输出格式如下：\n" +
                    "1998年：北京京东世纪贸易有限公司成立\n" +
                    "2004年：京东正式涉足电子商务领域";
            String content = AliYunUtil.makeApiCall(prompt);
            //String content = BaiduAiUtil.makeApiCall(prompt, "yi_34b_chat");
            //String content = TigerUtil.makeApiCall(message + "的发展历史，每行按照时间：事件格式输出，以冒号分隔", "");
            System.out.println("content:" + content);
            String[] lines = content.split("\n");

            for (int i = 0; i < lines.length; i++) {
                try {
                    String[] yearAndEvent = lines[i].split("：");
                    String event = yearAndEvent[1];
                    if (yearAndEvent.length > 3) {
                        for (int num = 2; num < yearAndEvent.length; num++) {
                            event += ":" + yearAndEvent[num];
                        }
                    }
                    timeLines.add(new Timeline(yearAndEvent[0], event));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                /*try {
                    if (i == 0) {
                        //do nothing
                    } else {
                        if (lines[i].contains("年") || lines[i].contains("代") || lines[i].contains("期") || lines[i].contains("世纪")) {
                            String[] yearAndEvent = lines[i].split("：");
                            String event = yearAndEvent[1];
                            if (yearAndEvent.length > 3) {
                                for (int num = 2; num < yearAndEvent.length; num++) {
                                    event += ":" + yearAndEvent[num];
                                }
                            }
                            timeLines.add(new Timeline(yearAndEvent[0].replace("*", ""), event));
                        } else if (lines[i].contains("至今")) {
                            if (lines[i].contains("：")) {
                                String[] yearAndEvent = lines[i].split("：");
                                timeLines.add(new Timeline(yearAndEvent[0], yearAndEvent[1]));
                            } else {
                                String[] yearAndEvent = lines[i].split("，");
                                timeLines.add(new Timeline(yearAndEvent[0], yearAndEvent[1]));
                            }

                        } else {
                            //do nothing
                        }
                    }
                } catch (Exception e) {
                    System.out.println("error:" + lines[i]);
                    e.printStackTrace();
                }*/

            }

            String name = BaiduAiUtil.makeApiCall(message + "的简单介绍，需要在20个字以内", "completions_pro");
            System.out.println("name:" + name);
            Cache.nameHash.put(message, name);
            Cache.timeLines.put(message, timeLines);
            session.getAsyncRemote().sendText(message);
        }

    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("WebSocket Closed: " + session.getId());
        //stopHeartbeat(); // 停止心跳
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket Error: " + session.getId());
        throwable.printStackTrace();
    }

    // 发送心跳消息
    private void sendHeartbeat(Session session) {
        try {
            session.getBasicRemote().sendText("heartbeat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 开始心跳定时器
    private void startHeartbeat(Session session) {
        heartbeatThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // 每5秒发送一次心跳
                    sendHeartbeat(session);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        heartbeatThread.start();
    }

    // 停止心跳定时器
    private void stopHeartbeat() {
        if (heartbeatThread != null && heartbeatThread.isAlive()) {
            heartbeatThread.interrupt();
        }
    }

    //发送信息方法
    private void sendMessage(Session session) {
        String message = "";
        //发送文本消息
        session.getAsyncRemote().sendText(message);
        Object obj = null;
        //发送对象消息，会尝试使用Encoder编码
        session.getAsyncRemote().sendObject(obj);
    }
}


