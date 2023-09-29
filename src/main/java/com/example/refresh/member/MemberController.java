package com.example.refresh.member;

import com.example.refresh.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
        String cookie = memberService.createCookie(token.refreshToken).toString();
        log.info("access=" + token.accessToken);
        log.info("refresh=" + token.refreshToken);
        return ResponseEntity.ok()
                .header(JwtProvider.HEADER, token.accessToken)
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body("ok");
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@CookieValue("refreshToken") String refreshToken){
        memberService.reissueToken(refreshToken);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        memberService.logout();
        var response = "";
        return ResponseEntity.ok().body(response);
    }
}
