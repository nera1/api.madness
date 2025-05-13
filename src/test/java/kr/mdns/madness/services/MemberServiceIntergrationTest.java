package kr.mdns.madness.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.SignupRequestDto;
import kr.mdns.madness.repository.MemberRepository;

@ActiveProfiles("h2")
@SpringBootTest
public class MemberServiceIntergrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void clean() {
        // memberRepository.deleteAll();
    }

    @Test
    void register_persistsMemberInH2() {
        SignupRequestDto dto = new SignupRequestDto("int@madness.com", "intTester", "Qwerty2@");

        Member saved = memberService.register(dto);

        Optional<Member> found = memberRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "멤버가 실제로 저장되어야 합니다");
        assertEquals(dto.getEmail(), found.get().getEmail());
        assertEquals(dto.getNickname(), found.get().getNickname());
        assertNotEquals(dto.getPassword(), found.get().getPassword());
    }

    @Test
    void testIsEmailDuplicate_false() {
        assertFalse(memberService.isEmailDuplicate("double@madness.com"));
    }

    @Test
    void testIsEmailDuplicate_true() {
        assertTrue(memberService.isEmailDuplicate("int@madness.com"));
    }

    @Test
    void testIsNicknameDuplicate_false() {
        assertFalse(memberService.isNicknameDuplicate("doubleTester"));
    }

    @Test
    void testIsNicknameDuplicate_true() {
        assertTrue(memberService.isNicknameDuplicate("intTester"));
    }
}
