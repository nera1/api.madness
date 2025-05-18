package kr.mdns.madness.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

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

import com.querydsl.core.types.dsl.BooleanExpression;

import kr.mdns.madness.domain.QMember;
import kr.mdns.madness.dto.DuplicateCheckResponseDto;
import kr.mdns.madness.dto.SignupRequestDto;
import kr.mdns.madness.dto.SignupResponseDto;
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

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void logUrl() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println(">>> Test is connecting to: " +
                    conn.getMetaData().getURL());
        }
    }

    @Test
    void post_memberSignupTest() {
        SignupRequestDto req = SignupRequestDto.builder()
                .email("nera@madn.es")
                .nickname("asdf11")
                .password("1q2w3e4r!!!")
                .build();

        HttpEntity<SignupRequestDto> request = new HttpEntity<>(req);

        ResponseEntity<ApiResponse<SignupResponseDto>> res = rest.exchange(
                "/member",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponse<SignupResponseDto> body = res.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCode()).isEqualTo(0);
        assertThat(body.getMessage()).isEqualTo("signup success");

        SignupResponseDto data = body.getData();
        assertThat(data.getEmail()).isEqualTo(req.getEmail());
        assertThat(data.getNickname()).isEqualTo(req.getNickname());

        QMember qm = QMember.member;
        BooleanExpression predicate = qm.email.eq(req.getEmail());
        assertTrue(memberRepository.exists(predicate));
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
}
