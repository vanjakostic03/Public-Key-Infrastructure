package com.ftn.pki.controllers.authentication;


import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    @PostMapping("/token")
    public ResponseEntity<?> exchangeCode(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        RestTemplate rest = new RestTemplate();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("grant_type", "authorization_code");
        params.add("client_id", "pki-backend");
        params.add("client_secret", "Vgyu0xweXpvG7D0jrggJZnR3RM09jzBC");
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:4200");

        ResponseEntity<Map> response =
                rest.postForEntity(
                        "http://localhost:8080/realms/pki/protocol/openid-connect/token",
                        params,
                        Map.class
                );

        return ResponseEntity.ok(response.getBody());
    }

}
