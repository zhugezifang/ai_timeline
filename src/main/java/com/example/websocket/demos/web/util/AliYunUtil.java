package com.example.websocket.demos.web.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.bailian20230601.Client;
import com.aliyun.bailian20230601.models.CreateTokenRequest;
import com.aliyun.bailian20230601.models.CreateTokenResponse;
import com.aliyun.bailian20230601.models.CreateTokenResponseBody;
import com.aliyun.teaopenapi.models.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

public class AliYunUtil {
    public static String createToken() {
        String accessKeyId = "";
        String accessKeySecret = "";
        String agentKey = "";
        String endpoint = "bailian.cn-beijing.aliyuncs.com";

        Config config = new Config().setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint(endpoint);

        try {
            Client client = new Client(config);
            CreateTokenRequest request = new CreateTokenRequest().setAgentKey(agentKey);
            CreateTokenResponse response = client.createToken(request);
            CreateTokenResponseBody body = response.getBody();
            if (body == null) {
                String error = "create token error";
                throw new RuntimeException(error);
            }

            if (!body.success) {
                String requestId = body.requestId;
                if (StringUtils.isBlank(requestId)) {
                    requestId = response.getHeaders().get("x-acs-request-id");
                }

                String error = "failed to create token, reason: " + body.message + " RequestId: " + requestId;
                throw new RuntimeException(error);
            }

            CreateTokenResponseBody.CreateTokenResponseBodyData data = body.getData();
            if (data == null) {
                throw new RuntimeException("create token error, data is null");
            }

            System.out.printf("token: %s, expiredTime : %d", data.getToken(), data.getExpiredTime());
            return data.token;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final String ENDPOINT = "https://bailian.aliyuncs.com/v2/app/completions";
    private static final String REQUEST_ID = "<RequestId>"; // 替换为实际的请求ID
    //private static final String API_KEY = "<token>"; // 替换为您的实际Token
    private static final String APP_ID = ""; // 替换为实际的应用ID

    public static String makeApiCall(String msg) throws Exception {
        System.out.println("msg:" + msg);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("RequestId", System.currentTimeMillis());
        requestBodyJson.put("AppId", APP_ID);

        JSONArray messagesJsonArray = new JSONArray();
        /*JSONObject systemMessage = new JSONObject();
        systemMessage.put("Role", "system");
        systemMessage.put("Content", "你是一名历史学家");
        messagesJsonArray.add(systemMessage);*/

        JSONObject userMessage = new JSONObject();
        userMessage.put("Role", "user");
        userMessage.put("Content", msg);
        messagesJsonArray.add(userMessage);

        requestBodyJson.put("Messages", messagesJsonArray);

        JSONObject parametersJson = new JSONObject();
        parametersJson.put("ResultFormat", "message");
        requestBodyJson.put("Parameters", parametersJson);

        HttpPost httpPost = new HttpPost(ENDPOINT);
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.setHeader("Authorization", "Bearer " + createToken());
        httpPost.setHeader("Accept-charset", "utf-8");

        StringEntity entity = new StringEntity(requestBodyJson.toString(), StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                //System.out.println("Response Content: " + EntityUtils.toString(responseEntity));
                String result = EntityUtils.toString(responseEntity);
                System.out.println("result:" + result);

                JSONObject jsonObject = JSONObject.parseObject(result);
                JSONObject dataObject = jsonObject.getJSONObject("Data");
                JSONObject messageObject = dataObject.getJSONArray("Choices").getJSONObject(0).getJSONObject("Message");
                String content = messageObject.getString("Content");
                System.out.println("content:" + content);
                return content;
                /*JSONObject jsonObject = JSONObject.parseObject(result);
                String data = jsonObject.getString("Data");
                JSONObject dataJson = JSONObject.parseObject(data);
                String Choices = dataJson.getString("Choices");
                JSONArray jsonArray=JSONArray.parseArray(Choices);
                System.out.println("Choices:" + dataJson.getString("Choices"));*/
            }
            EntityUtils.consume(responseEntity);
        } finally {
            httpClient.close();
        }
        return null;
    }

    public static String makeApiCallStream(String msg) throws Exception {

        System.out.println("msg:" + msg);
        CloseableHttpClient httpClient = HttpClients.createDefault();

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("RequestId", System.currentTimeMillis());
        requestBodyJson.put("AppId", APP_ID);
        requestBodyJson.put("Stream", true);

        JSONArray messagesJsonArray = new JSONArray();
        /*JSONObject systemMessage = new JSONObject();
        systemMessage.put("Role", "system");
        systemMessage.put("Content", "你是一名历史学家");
        messagesJsonArray.add(systemMessage);*/

        JSONObject userMessage = new JSONObject();
        userMessage.put("Role", "user");
        userMessage.put("Content", msg);
        messagesJsonArray.add(userMessage);

        requestBodyJson.put("Messages", messagesJsonArray);

        JSONObject parametersJson = new JSONObject();
        parametersJson.put("ResultFormat", "message");
        requestBodyJson.put("Parameters", parametersJson);

        HttpPost httpPost = new HttpPost(ENDPOINT);
        httpPost.setHeader("Accept", "text/event-stream");
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.setHeader("Authorization", "Bearer " + createToken());
        httpPost.setHeader("Accept-charset", "utf-8");

        StringEntity entity = new StringEntity(requestBodyJson.toString(), StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                try {
                    //System.out.println("Response Content: " + EntityUtils.toString(responseEntity));
                    String result = EntityUtils.toString(responseEntity);
                    System.out.println("result:" + result);
                    /*JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONObject dataObject = jsonObject.getJSONObject("Data");
                    JSONObject messageObject = dataObject.getJSONArray("Choices").getJSONObject(0).getJSONObject("Message");
                    String content = messageObject.getString("Content");
                    System.out.println("content:" + content);*/
                    return result;
                }catch (Exception e){
                    return null;
                }
                /*JSONObject jsonObject = JSONObject.parseObject(result);
                String data = jsonObject.getString("Data");
                JSONObject dataJson = JSONObject.parseObject(data);
                String Choices = dataJson.getString("Choices");
                JSONArray jsonArray=JSONArray.parseArray(Choices);
                System.out.println("Choices:" + dataJson.getString("Choices"));*/
            }
            EntityUtils.consume(responseEntity);
        } finally {
            httpClient.close();
        }
        return null;

    }

}
