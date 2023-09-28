package com.example.refresh.member;

public class MemberRequest {

    public record JoinDto(
            String email,
            String name,
            String password
    ){
        public Member toEntity(){
            return Member.builder()
                    .email(email)
                    .name(name)
                    .password(password)
                    .build();
        }
    }

    public record LoginDto(
            String email,
            String password
    ){}
}
