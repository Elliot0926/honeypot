package org.cloud.honeypot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.cloud.honeypot.dto.MemberDto;

@Mapper
public interface MemberMapper {
    
    // 이메일 중복 체크
    int countByEmail(String email);
    
    // 회원가입
    int insertMember(MemberDto memberDto);
    
    // 로그인 (이메일로 회원 조회)
    MemberDto selectMemberByEmail(String email);
    
    // 회원 정보 조회 (ID로)
    MemberDto selectMemberById(Long memberId);
    
    // 회원 정보 수정
    int updateMember(MemberDto memberDto);
    
    // 회원 탈퇴
    int deleteMember(Long memberId);
    
}