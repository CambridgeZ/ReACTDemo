package org.example.Tools;

import dev.langchain4j.agent.tool.Tool;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import dev.langchain4j.web.search.WebSearchTool;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;

public class WebSearchTools {

    @Tool(name = "web_search", value = "在网络上搜索关键词相关的信息")
    String searchOnline(String keyword) {

        System.out.println("搜索引擎被调用");

        Map<String, Object> optionalParameters = new HashMap<>();
        optionalParameters.put("region", "cn");  // 设置为中国地区
        optionalParameters.put("language", "zh");  // 设置为中文
        optionalParameters.put("domain", "baidu.com");  // 使用百度域名

        SearchApiWebSearchEngine searchApiWebSearchEngine = SearchApiWebSearchEngine.builder()
                .apiKey(System.getenv("BAIDU_SEARCH_API"))
                .engine("baidu")
                .optionalParameters(optionalParameters)
                .build();

        WebSearchTool webSearchTool = WebSearchTool.from(searchApiWebSearchEngine);
        return webSearchTool.searchWeb(keyword);
    }

    @Tool(name = "fetch_webpage", value = "获取指定URL的网页HTML内容")
    String fetchWebPage(String urlString) {
        System.out.println("网页内容抓取工具被调用: " + urlString);
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "获取网页内容失败: " + e.getMessage();
        }
        return content.toString();
    }


}
