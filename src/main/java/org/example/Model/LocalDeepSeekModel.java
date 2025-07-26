package org.example.Model;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.example.Agent.ReActAgent;
import org.example.Tools.WebSearchTools;

public class LocalDeepSeekModel {


    public static ReActAgent create() {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("DEEPSEEK_API_KEY")) //
                .baseUrl("https://api.deepseek.com") // DeepSeek本地API地址
                .modelName("deepseek-chat")
                .temperature(0.2)
                .maxTokens(2000)
                .build();

        ReActAgent original =  AiServices.builder(ReActAgent.class)
                .chatModel(model)
                .tools(new WebSearchTools())  // 注册工具
                .build();

        return new ReActAgent() {
            @Override
            public String research(String query) {
                // ====== 你自己的前置逻辑 ======
                System.out.println("开始研究: " + query);

                String promptFirstStep = """
                        你是一名深度研究助理，请基于以下用户问题制定一个详细的研究计划：
                        - 明确需要查找的关键信息点
                        - 给出具体的检索思路（可以包含关键字、潜在的数据源）
                        - 不要直接回答问题，只输出计划步骤
                         
                        用户问题：
                        """;

                String Answer = original.research(query);
                int maxStep = 20;
                for (int i = 0; i < maxStep; i++) {
                    String ReasonPrompt = """
                             请根据以下已有研究计划和最新结果，进行推理：
                             1. 分析是否还有未覆盖的重要信息点
                             2. 设计新的检索方案（具体要检索什么、去哪里检索）
                             3. 重点：如果结果中包含链接，请规划如何使用查看网页工具获取页面内容
                             只输出你的新的检索方案和推理过程，不要直接回答用户问题。
                             
                             现有内容：
                            """;
                    Answer = original.research(ReasonPrompt + Answer);
                    String ActPrompt = """
                             请根据上一步生成的检索方案：
                             - 执行信息整理和整合
                             - 输出一个更完整的回答
                             - 在回答中包含参考信息和检索到的原始链接
                            """;
                    Answer = original.research(ActPrompt + Answer);
                    String ObservePrompt = """
                            请只回答“是”或“不是”：
                            当前结果是否足够完整，是否还需要继续检索？
                            """;
                    String isNeedContinue = original.research(ObservePrompt + Answer);
                    if(isNeedContinue.equals("不是")){
                        break;
                    }
                }

                return Answer;
            }
        };


    }
}
