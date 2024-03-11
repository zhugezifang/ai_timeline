/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.websocket.demos.web;

import com.example.websocket.demos.web.config.Cache;
import com.example.websocket.demos.web.config.Timeline;
import com.example.websocket.demos.web.util.IpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@Controller
public class BasicController {
    // http://127.0.0.1:8080/html
    @RequestMapping("")
    public String html(HttpServletRequest request, ModelMap map) {
        String ipAddress = IpUtil.getIpAddr(request);
        System.out.println("ipAddress:" + ipAddress);
        map.addAttribute("ip", ipAddress);
        return "test";
    }

    @RequestMapping("/timeline")
    public String timeline(ModelMap map, String name) {
        System.out.println("timeline:" + name);
        map.addAttribute("title", name);

        /*LinkedList<Timeline> timeLines = new LinkedList<>();
        timeLines.add(new Timeline("2023年1月","上线 AI 阅读功能，支持 AI 解读多种格式（PDF、word、TXT、 网页链接等）"));
        timeLines.add(new Timeline("2023年3月","AI 对话功能，各种复杂文档的阅读难题，用户随意问， AI 快速精准解答，提升文档阅读效率"));
        timeLines.add(new Timeline("2023年5月","笔记功能，AI 阅读和个人总结一步到位"));
        timeLines.add(new Timeline("2023年6月","文档特殊公式识别，学术文档识别精准度提升"));
        timeLines.add(new Timeline("2023年8月","文档支持快捷操作，一键解读、摘要总结、引用解读等快捷功能"));
        timeLines.add(new Timeline("2023年11月","界面整体优化，交互体验更细致"));
        timeLines.add(new Timeline("2024年01月","扩充更多阅读场景支持（合同审查、法律法规、简历分析、财经解读）"));
        timeLines.add(new Timeline("2024年02月","文档翻译，外文文档快速翻译，减轻阅读负担，支持多种语言互译"));
        timeLines.add(new Timeline("2024年03月","底层优化了 AI 文档的解析精准度，提升了 AI 对话表达力"));
        map.addAttribute("name","包阅AI：AI论文阅读工具(AI时间线生成)");
        map.addAttribute("list", timeLines);*/
        map.addAttribute("name", Cache.nameHash.get(name) + "(AI时间线生成)");
        map.addAttribute("list", Cache.timeLines.get(name));
        return "timeline";
    }

}
