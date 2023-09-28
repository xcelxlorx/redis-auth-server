package com.example.refresh.member;

import lombok.RequiredArgsConstructor;
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

    public void login(MemberRequest.LoginDto requestDto){

    }

    public void logout(){

    }
}
