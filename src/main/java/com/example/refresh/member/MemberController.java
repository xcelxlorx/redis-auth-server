package com.example.refresh.member;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/members")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<?> join(MemberRequest.JoinDto requestDto){
        memberService.join(requestDto);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(MemberRequest.LoginDto requestDto){
        memberService.login(requestDto);
        return ResponseEntity.ok().body("ok");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(){
        memberService.logout();
        var response = "";
        return ResponseEntity.ok().body(response);
    }
}
