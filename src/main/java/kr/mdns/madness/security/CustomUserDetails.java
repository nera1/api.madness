package kr.mdns.madness.security;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.mdns.madness.domain.Member;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    public LocalDateTime getCreatedAt() {
        return member.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return member.getUpdatedAt();
    }

    public Long getId() {
        return member.getId();
    }

    public Member getMember() {
        return member;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

}
