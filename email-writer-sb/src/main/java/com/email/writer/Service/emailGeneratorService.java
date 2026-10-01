package com.email.writer.Service;

import com.email.writer.DTO.emailRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@Data
public class emailGeneratorService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String generateEmailReply(emailRequest emailRequest )
    {
        //build the prompt
        String prompt = buildPrompt(emailRequest);

        // craft a request
        Map<String , Object> requestBody = Map.of(
                "contents" , new Object[]
                {
                        Map.of("parts", new Object[]{
                                Map.of("text" ,prompt)
                        })
                }
        );

        // Do Request And Get Response
        String response = webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .header("Content-Type" , "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Extract Response And Return
        return extractResponseContent(response);


    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asString();
        } catch (Exception e)
        {
            return "Error processing request: " +e.getMessage();
        }
    }

    private String buildPrompt(emailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a Professional email reply for the following email content. Please dont generate subject line ");
        if( emailRequest.getTone() != null && !emailRequest.getTone().isEmpty() )
        {
            prompt.append("Use a ").append(emailRequest.getTone()).append("tone");
        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
