package kr.mdns.madness.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.mdns.madness.domain.Member;
import kr.mdns.madness.dto.SignupRequestDto;
import kr.mdns.madness.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean isNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Transactional
    public Member register(SignupRequestDto req) {
        if (isEmailDuplicate(req.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (isNicknameDuplicate(req.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String encodedPw = passwordEncoder.encode(req.getPassword());

        Member member = Member.builder()
                .email(req.getEmail())
                .nickname(req.getNickname())
                .password(encodedPw)
                .build();

        return memberRepository.save(member);
    }

}
