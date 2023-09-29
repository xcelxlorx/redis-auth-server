package com.example.refresh.member;

import com.example.refresh.auth.JwtProvider;
import com.example.refresh.auth.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final RedisTemplate<String, Long> redisTemplate;

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

    public void logout(){

    }

    private Token issueToken(Member member) {
        String accessToken = JwtProvider.createAccessToken(member);
        String refreshToken = JwtProvider.createRefreshToken(member);
        saveToken(refreshToken, member);
        return new Token(accessToken, refreshToken);
    }

    private void saveToken(String refreshToken, Member member){
        ValueOperations<String, Long> valueOps = redisTemplate.opsForValue();
        valueOps.set(refreshToken, member.getId());
        redisTemplate.expire(refreshToken, 60L, TimeUnit.SECONDS);
    }
}
