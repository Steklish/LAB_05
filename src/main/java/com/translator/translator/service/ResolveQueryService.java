package com.translator.translator.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

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
            
            JsonNode rootNode = objectMapper.readTree(json);

            // Accessing properties
            JsonNode firstElement = rootNode.get(0);
            JsonNode nestedArray = firstElement.get(0);
            String responseText = nestedArray.get(0).asText();
            String queryText = nestedArray.get(1).asText();


            // URL-decode the response text if necessary
            String decodedResponseText = java.net.URLDecoder.decode(responseText, StandardCharsets.UTF_8.toString());
            String decodedQueryText = java.net.URLDecoder.decode(queryText, StandardCharsets.UTF_8.toString());

            return List.of(decodedResponseText, decodedQueryText, destLang, srcLan);
        } catch (RestClientException | UnsupportedEncodingException e) {
            return List.of("error", e.toString());
        }
    }
}