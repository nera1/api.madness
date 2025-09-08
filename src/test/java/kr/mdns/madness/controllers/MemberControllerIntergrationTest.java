package kr.mdns.madness.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import kr.mdns.madness.dto.DuplicateCheckResponseDto;
import kr.mdns.madness.repository.MemberRepository;
import kr.mdns.madness.response.ApiResponse;

@Profile("h2")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerIntergrationTest {
    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {

    }

    @Test
    void post_memberSignupTest() {

    }

    @Test
    void get_memberCheckEmailTest() {
        HttpEntity<?> request = new HttpEntity<>(null);
        ResponseEntity<ApiResponse<DuplicateCheckResponseDto>> res = rest.exchange(
                "/member/check/email?email=nera@madness.com",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<DuplicateCheckResponseDto> body = res.getBody();
        assertThat(body).isNotNull();

        DuplicateCheckResponseDto data = body.getData();
        assertThat(data.isDuplicate()).isEqualTo(true);
    }

    @Test
    void get_memberCheckNickNameTest() {
        HttpEntity<?> request = new HttpEntity<>(null);
        ResponseEntity<ApiResponse<DuplicateCheckResponseDto>> res = rest.exchange(
                "/member/check/nickname?nickname=nera",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                });
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<DuplicateCheckResponseDto> body = res.getBody();
        assertThat(body).isNotNull();

        DuplicateCheckResponseDto data = body.getData();
        assertThat(data.isDuplicate()).isEqualTo(true);
    }
}
