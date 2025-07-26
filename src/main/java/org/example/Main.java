package org.example;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.example.Model.LocalDeepSeekModel;

import org.example.Model.LocalDeepSeekModel;
import org.example.Agent.ReActAgent;

public class Main {
    private ReActAgent agent;

    public static void main(String[] args) {
        Main main = new Main();
        main.setAgent(LocalDeepSeekModel.create());

        String query = "deep research 技术在大模型中的应用";
        String answer = main.chat(query);
        System.out.println("Final Answer:\n" + answer);
    }

    public String chat(String message) {
        return agent.research(message);
    }

    public void setAgent(ReActAgent agent) {
        this.agent = agent;
    }
}
