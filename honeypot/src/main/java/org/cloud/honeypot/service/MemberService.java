package org.cloud.honeypot.service;

import org.cloud.honeypot.dto.MemberDto;

public interface MemberService {
    
    // 회원가입
    int join(MemberDto memberDto);
    
    // 이메일 중복 체크
    boolean checkEmail(String email);
    
    // 로그인
    MemberDto login(String email, String password);
    
    // 회원 정보 조회
    MemberDto getMember(Long memberId);
    
    // 회원 정보 수정
    int updateMember(MemberDto memberDto);
    
    // 회원 탈퇴
    int deleteMember(Long memberId);
    
}