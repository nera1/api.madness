package api.madn.es.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {
    @PostMapping("/signin")
    fun login(): String {
        return "login"
    }

    @PostMapping("/register")
    fun register(): String {
        return "register"
    }

    @GetMapping("/signout")
    fun logout(): String {
        return "logout"
    }
}