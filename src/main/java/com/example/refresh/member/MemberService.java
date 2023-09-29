package com.example.refresh.member;

import com.example.refresh.auth.JwtProvider;
import com.example.refresh.auth.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final RedisTemplate<String, String> redisTemplate;

    public void join(MemberRequest.JoinDto requestDto){
        Member member = requestDto.toEntity();
        memberRepository.save(member);
    }

    public Token login(MemberRequest.LoginDto requestDto){
        Member member = memberRepository.findByEmail(requestDto.email());
        return issueToken(member);
    }

    public Token reissueToken(String refreshToken){
        Long memberId = JwtProvider.decodeRefreshToken(refreshToken);
        Member member = memberRepository.findById(memberId).orElseThrow();
        return issueToken(member);
    }

    public ResponseCookie createCookie(String token){
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .maxAge(JwtProvider.getRefreshExpires())
                .build();
    }

    public void logout(Long id){
        redisTemplate.delete(id.toString());
    }

    private Token issueToken(Member member) {
        String accessToken = JwtProvider.createAccessToken(member);
        String refreshToken = JwtProvider.createRefreshToken(member);
        saveToken(refreshToken, member);
        return new Token(accessToken, refreshToken);
    }

    private void saveToken(String refreshToken, Member member){
        redisTemplate.opsForValue().set(member.getId().toString(), refreshToken);
        redisTemplate.expire(member.getId().toString(), 60L, TimeUnit.SECONDS);
    }
}
