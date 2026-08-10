package kko.traveldiary_api.city.adaptor.ai;

import kko.traveldiary_api.city.application.required.CityDescriptionGenerator;
import kko.traveldiary_api.city.application.required.CityImageGenerator;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 실제 생성형 AI 어댑터의 수동 빈 등록.
 * {@code prod} 프로파일에서만 등록되며, 그 외(local/dev/test)에서는
 * {@link FakeGenerativeAiConfig} 의 Fake 구현이 주입되어 실제 AI 를 호출하지 않는다.
 */
@Configuration
public class GenerativeAiConfig {

    @Bean(name = "claudeChatClient")
    @Profile("prod")
    public ChatClient claudeChatClient(AnthropicChatModel anthropicChatModel) {
        return ChatClient.create(anthropicChatModel);
    }

    @Bean
    @Profile("prod")
    public CityDescriptionGenerator cityDescriptionGenerator(
            @Qualifier("claudeChatClient") ChatClient claudeChatClient) {
        return new CityDescriptionAIGenerator(claudeChatClient);
    }

    @Bean
    @Profile("prod")
    public CityImageGenerator cityImageGenerator(ImageModel imageModel) {
        return new CityImageAiGenerator(imageModel);
    }
}
