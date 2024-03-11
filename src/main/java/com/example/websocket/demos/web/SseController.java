package com.example.websocket.demos.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class SseController {
    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping("/article")
    public void sendArticle(HttpServletResponse res) throws Exception {
        res.setContentType("text/event-stream;charset=UTF-8");
        Resource resource = resourceLoader.getResource("classpath:1.txt");
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            // 这里用空格来将一行内容分为多个单词输出
            String delimiter = " ";
            String[] words = line.split(delimiter);
            // 为了支持将中文句子分为单个汉子输出
            if (words.length == 1) {
                delimiter = "";
                words = line.split(delimiter);
            }
            for (int i = 0; i < words.length; i++) {
                // 每次只输出一个词,每个片段以data: 开头，以\n\n结尾
                res.getWriter().write("data: " + words[i] + delimiter + "\n\n");
                res.getWriter().flush();
                // 睡眠100ms,便于观察效果
                Thread.sleep(100);
            }
            // 由于每次读的是一行数据，因此输出一个换行
            res.getWriter().write("data: </br>\n\n");
        }
        // 也用[done]来标识结束
        res.getWriter().write("data: [done]\n\n");
        res.getWriter().flush();
    }
}
