package com.epam.gym_crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@ApiIgnore
public class SwaggerResourceController {

    @GetMapping("/swagger-resources/configuration/ui")
    public ResponseEntity<Map<String, Object>> uiConfiguration() {
        Map<String, Object> uiConfig = new HashMap<>();
        uiConfig.put("deepLinking", true);
        uiConfig.put("displayOperationId", false);
        uiConfig.put("defaultModelsExpandDepth", 1);
        uiConfig.put("defaultModelExpandDepth", 1);
        uiConfig.put("defaultModelRendering", "example");
        uiConfig.put("displayRequestDuration", false);
        uiConfig.put("docExpansion", "none");
        uiConfig.put("filter", false);
        uiConfig.put("operationsSorter", "alpha");
        uiConfig.put("showExtensions", false);
        uiConfig.put("tagsSorter", "alpha");
        uiConfig.put("validatorUrl", "");
        uiConfig.put("apisSorter", "alpha");
        uiConfig.put("jsonEditor", false);
        uiConfig.put("showRequestHeaders", false);
        uiConfig.put("supportedSubmitMethods", new String[]{"get", "put", "post", "delete", "options", "head", "patch", "trace"});
        return new ResponseEntity<>(uiConfig, HttpStatus.OK);
    }

    @GetMapping("/swagger-resources/configuration/security")
    public ResponseEntity<Map<String, Object>> securityConfiguration() {
        Map<String, Object> securityConfig = new HashMap<>();
        return new ResponseEntity<>(securityConfig, HttpStatus.OK);
    }

    @GetMapping("/swagger-resources")
    public ResponseEntity<List<Map<String, Object>>> swaggerResources() {
        List<Map<String, Object>> resources = new ArrayList<>();
        Map<String, Object> resource = new HashMap<>();
        resource.put("name", "default");
        resource.put("url", "/v2/api-docs");
        resource.put("swaggerVersion", "2.0");
        resource.put("location", "/v2/api-docs");
        resources.add(resource);
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
}