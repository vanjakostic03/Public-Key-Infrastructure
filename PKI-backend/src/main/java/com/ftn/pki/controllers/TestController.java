package com.ftn.pki.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public Map<String, Object> test(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> map = new HashMap<>();
        map.put("sub", jwt.getSubject());
        map.put("roles", jwt.getClaimAsStringList("realm_access.roles"));
        return map;
    }

    @GetMapping("/testt")
    public Map<Integer, String> testt() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1,"sub");
        return map;
    }
}

