package kr.mdns.madness.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.SignupRequestDto;
import kr.mdns.madness.dto.SignupResponseDto;
import kr.mdns.madness.response.ApiResponse;
import kr.mdns.madness.services.MemberService;

@WebMvcTest(MemberController.class)
public class MemberControllerTest {
        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper objectMapper;

        @MockitoBean
        MemberService memberService;

        @MockitoBean
        private kr.mdns.madness.config.CorsProperties corsProperties;

        @Test
        @DisplayName("POST /member → 201 Created + ApiResponse<SignupResponseDto> 반환")
        void register_success() throws Exception {

                SignupRequestDto req = SignupRequestDto.builder()
                                .email("nera@madness.com")
                                .nickname("nera1")
                                .password("P@ssw0rd1!")
                                .build();

                Member savedMember = Member.builder()
                                .email(req.getEmail())
                                .nickname(req.getNickname())
                                .password("HASH")
                                .build();
                savedMember.setId(3L);

                given(memberService.register(any(SignupRequestDto.class)))
                                .willReturn(savedMember);

                SignupResponseDto payload = SignupResponseDto.builder()
                                .email(req.getEmail())
                                .nickname(req.getNickname())
                                .build();
                ApiResponse<SignupResponseDto> expected = new ApiResponse<>(0, "signup success", payload);

                mockMvc.perform(post("/member")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
        }
}
