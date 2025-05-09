package kr.mdns.madness.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.dsl.BooleanExpression;

import kr.mdns.madness.domain.Member;
import kr.mdns.madness.domain.QMember;
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
        QMember m = QMember.member;
        BooleanExpression predicate = m.email.eq(email);
        return memberRepository.exists(predicate);
    }

    public boolean isNicknameDuplicate(String nickname) {
        QMember m = QMember.member;
        BooleanExpression predicate = m.nickname.eq(nickname);
        return memberRepository.exists(predicate);
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
