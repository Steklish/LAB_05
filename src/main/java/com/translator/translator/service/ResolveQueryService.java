package com.translator.translator.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.translator.translator.dto.request.BulkTranslationItemRequest;
import com.translator.translator.dto.response.BulkTranslationItemResponse;

@Service
public class ResolveQueryService {
    private static final String URL = "https://translate.googleapis.com/translate_a/single?client=gtx&sl={sourceLang}&tl={targetLang}&dt=t&q={text}";
 
    public List<String> getTranslation(String srcLan, String destLang, String text) 
            throws JsonMappingException, JsonProcessingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.getForEntity(
                    URL, String.class, srcLan, destLang, text);

            JsonNode rootNode = objectMapper.readTree(response.getBody());
            System.out.print("root node");
            System.out.println(rootNode);
            int len = rootNode.get(0).size();
            System.out.print("length of response");
            System.out.println(len);
            String decodedResponseText = "";
            for (int i = 0; i < len; i++) {
                JsonNode translatedTextNode = rootNode.get(0).get(i).get(0);
                decodedResponseText += translatedTextNode.asText();
                
            }
            System.out.println(decodedResponseText);
            return List.of(
                decodedResponseText
            );
        } catch (RestClientException | JsonParseException e) {
            return List.of("error", e.toString());
        }
    }
    

    public List<BulkTranslationItemResponse> getBulkTranslations(List<BulkTranslationItemRequest> requests) throws JsonMappingException, JsonProcessingException {
        System.out.println("Resolving bulk translation for " + requests.size() + " items.");

        return requests.stream()
                .map(requestItem -> {
                    try {
                        List<String> translatedList = getTranslation(
                            requestItem.getSrcLan(),
                            requestItem.getDestLang(),
                            requestItem.getText()
                        );
                        String translatedText = translatedList.isEmpty() ? "" : translatedList.get(0); // Get the first translation

                        BulkTranslationItemResponse responseItem = new BulkTranslationItemResponse();
                        responseItem.setOriginalLanguage(requestItem.getSrcLan());
                        responseItem.setTranslatedLanguage(requestItem.getDestLang());
                        responseItem.setOriginalText(requestItem.getText());
                        responseItem.setTranslatedText(translatedText);
                        responseItem.setTranslationId(null); 

                        return responseItem;

                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error translating item: " + requestItem.getText(), e);
                    }
                })
                .collect(Collectors.toList());
    }

}