package kr.mdns.madness.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.DuplicateCheckResponseDto;
import kr.mdns.madness.dto.SignupRequestDto;
import kr.mdns.madness.dto.SignupResponseDto;
import kr.mdns.madness.response.ApiResponse;
import kr.mdns.madness.services.MemberService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/member")
@Validated
@RequiredArgsConstructor
public class MemberController {
        private final MemberService memberService;

        @PostMapping
        public ResponseEntity<ApiResponse<SignupResponseDto>> register(
                        @Valid @RequestBody SignupRequestDto req) {
                Member saved = memberService.register(req);
                SignupResponseDto payload = SignupResponseDto
                                .builder()
                                .email(saved.getEmail())
                                .nickname(saved.getNickname())
                                .build();
                ApiResponse<SignupResponseDto> resp = new ApiResponse<SignupResponseDto>(0, "signup success", payload);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(resp);
        }

        @GetMapping("/check/nickname")
        public ResponseEntity<ApiResponse<DuplicateCheckResponseDto>> checkNickname(
                        @RequestParam String nickname) {
                boolean isDuplicate = memberService.isNicknameDuplicate(nickname);
                DuplicateCheckResponseDto payload = DuplicateCheckResponseDto.builder().isDuplicate(isDuplicate)
                                .build();
                ApiResponse<DuplicateCheckResponseDto> resp = new ApiResponse<>(0,
                                isDuplicate ? "nickname duplicate" : "nickname available", payload);
                return ResponseEntity.status(HttpStatus.OK).body(resp);
        }

        @GetMapping("/check/email")
        public ResponseEntity<ApiResponse<DuplicateCheckResponseDto>> checkEmail(@RequestParam String email) {
                boolean isDuplicate = memberService.isEmailDuplicate(email);
                DuplicateCheckResponseDto payload = DuplicateCheckResponseDto.builder().isDuplicate(isDuplicate)
                                .build();
                ApiResponse<DuplicateCheckResponseDto> resp = new ApiResponse<>(0,
                                isDuplicate ? "email duplicate" : "email available", payload);
                return ResponseEntity.status(HttpStatus.OK).body(resp);
        }

}
