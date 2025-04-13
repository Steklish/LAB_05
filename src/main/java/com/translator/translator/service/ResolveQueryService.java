package com.translator.translator.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.translator.translator.tools.HTTPRequestHandler;

@Service
public class ResolveQueryService {
    
    HTTPRequestHandler client = new HTTPRequestHandler();

    public List<String> getTranslation(String srcLan, String destLang, String text) 
            throws JsonMappingException, JsonProcessingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(client.getRequestForTranslation(srcLan, destLang, text), String.class);
            String json = response.getBody();

            // Validate if the response is JSON
            if (json == null || !json.trim().startsWith("{")) {
                throw new JsonParseException(null, "Response is not valid JSON");
            }

            JsonNode rootNode = objectMapper.readTree(json);

            // Accessing properties
            JsonNode firstElement = rootNode.get(0);
            JsonNode nestedArray = firstElement.get(0);
            String responseText = nestedArray.get(0).asText();
            String queryText = nestedArray.get(1).asText();


            // URL-decode the response text if necessary
            String decodedResponseText = java.net.URLDecoder.decode(responseText, StandardCharsets.UTF_8.toString());
            String decodedQueryText = java.net.URLDecoder.decode(queryText, StandardCharsets.UTF_8.toString());
            return List.of(
                "translated_text: " + decodedResponseText,
                "original_text: " + decodedQueryText,
                "destination_language: " + destLang,
                "source_language: " + srcLan,
                "raw_json: " + json
            );
        } catch (RestClientException | UnsupportedEncodingException | JsonParseException e) {
            return List.of("error", e.toString());
        }
    }
}