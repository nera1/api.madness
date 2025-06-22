package kr.mdns.madness.security;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import kr.mdns.madness.domain.Member;
import kr.mdns.madness.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // List<GrantedAuthority> authorities = List.of(
        // new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomUserDetails(member);
    }

    public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. ID: " + userId));
        return new CustomUserDetails(member);
    }
}
