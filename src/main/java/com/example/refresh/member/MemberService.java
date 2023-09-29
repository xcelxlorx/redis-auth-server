package com.example.refresh.member;

import com.example.refresh.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public void join(MemberRequest.JoinDto requestDto){
        Member member = requestDto.toEntity();
        memberRepository.save(member);
    }

    public Token login(MemberRequest.LoginDto requestDto){
        Member member = memberRepository.findByEmail(requestDto.email());
        return issueToken(member);
    }

    public void reissueToken(String refreshToken){

    }

    public ResponseCookie createCookie(String token){
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .maxAge(JwtProvider.getRefreshExpires())
                .build();
    }

    private Token issueToken(Member member) {
        String accessToken = JwtProvider.createAccessToken(member);
        String refreshToken = JwtProvider.createRefreshToken(member);
        return new Token(accessToken, refreshToken);
    }

    public void logout(){

    }
}
