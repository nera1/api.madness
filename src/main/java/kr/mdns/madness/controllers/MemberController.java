package kr.mdns.madness.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/member")
@Validated
public class MemberController {
    @PostMapping
    public String postMethodName(@RequestBody String entity) {
        return entity;
    }
}
