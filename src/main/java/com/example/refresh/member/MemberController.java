package com.example.refresh.member;

import com.example.refresh.auth.JwtProvider;
import com.example.refresh.auth.Token;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody MemberRequest.JoinDto requestDto){
        memberService.join(requestDto);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberRequest.LoginDto requestDto){
        Token token = memberService.login(requestDto);
        String cookie = memberService.createCookie(token.getRefreshToken()).toString();
        log.info("login access=" + token.getAccessToken());
        log.info("login refresh=" + token.getRefreshToken());
        return ResponseEntity.ok()
                .header(JwtProvider.HEADER, token.getAccessToken())
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body("ok");
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@CookieValue("refreshToken") String refreshToken){
        Token token = memberService.reissueToken(refreshToken);
        String cookie = memberService.createCookie(token.getRefreshToken()).toString();
        log.info("reissue access=" + token.getAccessToken());
        log.info("reissue refresh=" + token.getRefreshToken());
        return ResponseEntity.ok()
                .header(JwtProvider.HEADER, token.getAccessToken())
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body("ok");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        Long id = 1L; //임시 하드코딩
        memberService.logout(id);

        String cookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .build().toString();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body("ok");
    }
}
