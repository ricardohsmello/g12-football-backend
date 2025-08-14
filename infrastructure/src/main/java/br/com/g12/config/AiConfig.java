package br.com.g12.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

//  @Bean
//  public ChatMemory chatMemory() {
//    return MessageWindowChatMemory.builder()
//        .maxMessages(20)
//        .build();
//  }

  @Bean
  public ChatClient chatClient(OpenAiChatModel chatModel, ChatMemory chatMemory) {
    return ChatClient.builder(chatModel)
//        .defaultAdvisors(PromptChatMemoryAdvisor.builder(chatMemory).build())
        .build();
  }
}
