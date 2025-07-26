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
                        这是用户第一步传入的问题，请确定对于这个问题的研究方案
                        """;

                String Answer = original.research(query);
                int maxStep = 20;
                for (int i = 0; i < maxStep; i++) {
                    String ReasonPrompt = """
                            下面是之前大模型根据用户问题得到的结果，请根据结果进行进一步完善，设计新的检索和思考的方案
                            如果其中有链接我希望你能够使用获取网页的工具去查看其中涉及的文档
                            """;
                    Answer = original.research(ReasonPrompt + Answer);
                    String ActPrompt = """
                            这是之前大模型设计的新的检索方案，请根据此方案整理并获取相关的信息，并形成回答
                            在回答当中我希望你包含进去检索文档的链接
                            """;
                    Answer = original.research(ActPrompt + Answer);
                    String ObservePrompt = """
                            这是之前得到的结果，请告诉我是否需要继续检索，你的回答只有两种"是" 或者 “不是”
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
