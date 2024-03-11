package com.example.websocket;

import com.example.websocket.demos.web.config.Cache;
import com.example.websocket.demos.web.config.Timeline;
import com.example.websocket.demos.web.util.AliYunUtil;
import com.example.websocket.demos.web.util.BaiduAiUtil;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedList;

public class MainTest {
    public static final String API_KEY = "";
    public static final String SECRET_KEY = "";

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static void main(String[] args) throws Exception {

        String contentResult = null;
        contentResult = AliYunUtil.makeApiCallStream("根据下面的描述内容，推荐关联性比较强的5个电商的商品：明天是周一,工作日");

        System.out.println("contentResult:" + contentResult);

        /*String result = BaiduAiUtil.sendGet("");
        System.out.println(result);*/
        /*String message = "小红书";
        String name = BaiduAiUtil.makeApiCall(message + "的介绍，30个字以内","yi_34b_chat");
        System.out.println("name:" + name);
        String content = BaiduAiUtil.makeApiCall(message + "的发展历史，第一行介绍，后续行数按照时间：事件 格式输出","yi_34b_chat");
        System.out.println("content:" + content);
        String[] lines = content.split("\n");
        LinkedList<Timeline> timeLines = new LinkedList<>();
        for (int i = 0; i < lines.length; i++) {
            try {
                if (i == 0) {
                    //do nothing
                } else {
                    if (lines[i].contains("年")) {
                        String[] yearAndEvent = lines[i].split("：");
                        timeLines.add(new Timeline(yearAndEvent[0], yearAndEvent[1]));
                    } else if (lines[i].contains("至今")){
                        String[] yearAndEvent = lines[i].split("，");
                        timeLines.add(new Timeline(yearAndEvent[0], yearAndEvent[1]));
                    }else {
                        //do nothing
                    }
                }
            }catch (Exception e){
                System.out.println(lines[i]);
            }
        }
        int x=0;*/

        /*String accessToken = getAccessToken();
        System.out.println(accessToken);
        String text = "介绍一下你自己";
        String result = makeApiCall(accessToken, text);
        System.out.println(result);*/
    }

    public static String makeApiCall(String accessToken, String text) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token=" + accessToken);

        // 构造JSON数据
        JSONObject payload = new JSONObject();
        JSONArray messagesArray = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", text);
        messagesArray.put(message);
        payload.put("messages", messagesArray);

        // 设置HTTP POST请求的数据体和内容类型
        StringEntity input = new StringEntity(payload.toString(), "UTF-8");
        httpPost.setEntity(input);
        httpPost.setHeader("Content-Type", "application/json");

        // 执行POST请求并获取响应
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();

        // 处理响应内容（这里假设API返回的是UTF-8编码，不需要额外转换）
        String resultJson = EntityUtils.toString(entity, "UTF-8");
        EntityUtils.consume(entity); // 释放连接

        // 关闭HTTP客户端资源
        response.close();
        httpClient.close();

        // 解析JSON响应并返回结果
        JSONObject resultData = new JSONObject(resultJson);
        return resultData.getString("result");
    }

    public static void getMessag() throws Exception {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"messages\":[{\"role\":\"user\",\"content\":\"京东的发展历史时间线\"}],\"disable_search\":false,\"enable_citation\":false}");
        String token = getAccessToken();
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token=" + token)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        System.out.println(response.body().string());
    }

    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    static String getAccessToken() throws Exception {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }

}

