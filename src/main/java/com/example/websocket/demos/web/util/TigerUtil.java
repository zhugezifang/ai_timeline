package com.example.websocket.demos.web.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TigerUtil {

    public static String makeApiCall(String text, String model) throws Exception {
        // 实际API密钥替换此处占位符
        String API_KEY = "";

        // 目标URL与Python示例相同
        String url = "https://api.tigerbot.com/v1/chat/completions";

        // JSON负载数据
        JSONObject payload = new JSONObject();
        payload.put("model", "tigerbot-70b-chat");
        payload.put("query", text);

        // 创建HTTP客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 构建POST请求
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        httpPost.setHeader("Authorization", "Bearer " + API_KEY);
        httpPost.setHeader("Content-Type", "application/json");

        // 将JSON对象转换为StringEntity，并设置到POST请求体中
        StringEntity input = new StringEntity(payload.toString(), "utf-8");
        httpPost.setEntity(input);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            // 获取响应实体
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // 打印响应内容
                String json = EntityUtils.toString(entity);
                JSONObject jsonObject = JSONObject.parseObject(json);
                return jsonObject.getString("result");
                //System.out.println("Response Content: " + EntityUtils.toString(entity));
            }

            EntityUtils.consume(entity);  // 完全读取并关闭输入流
        } finally {
            httpClient.close();  // 关闭HTTP客户端
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        makeApiCall("", "");
    }

}
