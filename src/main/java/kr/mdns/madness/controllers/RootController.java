package kr.mdns.madness.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @GetMapping("/healthz")
    public ResponseEntity<String> healthz() {
        return ResponseEntity.ok("ok");
    }
}
