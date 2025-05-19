package kr.mdns.madness.services;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.querydsl.core.types.dsl.BooleanExpression;

import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.SignupRequestDto;
import kr.mdns.madness.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private final String TEST_EMAIL = "dummy@dude.com";
    private final String TEST_NICK = "Tester";
    private final String TEST_RAW_PWD = "A1q2w3e4r!!";

    @BeforeEach
    void setUp() {
        when(memberRepository.exists(any(BooleanExpression.class))).thenReturn(false);
        when(passwordEncoder.encode(TEST_RAW_PWD)).thenReturn("ENC(pw)");
        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Member.class));
    }

    @Test
    @DisplayName("이메일 중복 검사: 중복일 때 true return")
    void testIsEmailDuplicate_true() {
        when(memberRepository.exists(any(BooleanExpression.class))).thenReturn(false);
        when(passwordEncoder.encode(TEST_RAW_PWD)).thenReturn("ENC(pw)");
        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Member.class));

        when(memberRepository.exists(any(BooleanExpression.class))).thenReturn(true);
        assertTrue(memberService.isEmailDuplicate(TEST_EMAIL));
        verify(memberRepository).exists(any(BooleanExpression.class));
    }

    @Test
    @DisplayName("닉네임 중복 검사: 중복일 때 true return")
    void testIsNicknameDuplicate_true() {
        when(memberRepository.exists(any(BooleanExpression.class))).thenReturn(false);
        when(passwordEncoder.encode(TEST_RAW_PWD)).thenReturn("ENC(pw)");
        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Member.class));

        when(memberRepository.exists(any(BooleanExpression.class))).thenReturn(true);
        assertTrue(memberService.isNicknameDuplicate(TEST_NICK));
        verify(memberRepository).exists(any(BooleanExpression.class));
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testRegister_success() {
        when(memberRepository.exists(any(BooleanExpression.class))).thenReturn(false);
        when(passwordEncoder.encode(TEST_RAW_PWD)).thenReturn("ENC(pw)");
        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Member.class));

        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .email(TEST_EMAIL)
                .nickname(TEST_NICK)
                .password(TEST_RAW_PWD).build();

        Member m = memberService.register(signupRequestDto);
        assertEquals(m.getEmail(), signupRequestDto.getEmail());
        assertEquals(m.getNickname(), signupRequestDto.getNickname());
        assertNotEquals(m.getPassword(), signupRequestDto.getPassword());
        verify(memberRepository).save(any(Member.class));
        verify(memberRepository, times(2)).exists(any(BooleanExpression.class));
    }

}
