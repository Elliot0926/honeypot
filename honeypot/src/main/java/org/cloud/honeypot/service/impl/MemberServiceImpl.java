package org.cloud.honeypot.service.impl;

import org.cloud.honeypot.dto.MemberDto;
import org.cloud.honeypot.mapper.MemberMapper;
import org.cloud.honeypot.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    // 회원가입
    @Override
    public int join(MemberDto memberDto) {
        return memberMapper.insertMember(memberDto);
    }

    // 이메일 중복 체크
    @Override
    public boolean checkEmail(String email) {
        return memberMapper.countByEmail(email) > 0;
    }

    // 로그인
    @Override
    public MemberDto login(String email, String password) {
        MemberDto member = memberMapper.selectMemberByEmail(email);
        if (member != null && member.getPassword().equals(password)) {
            return member;
        }
        return null;
    }

    // 회원 정보 조회
    @Override
    public MemberDto getMember(Long memberId) {
        return memberMapper.selectMemberById(memberId);
    }

    // 회원 정보 수정
    @Override
    public int updateMember(MemberDto memberDto) {
        return memberMapper.updateMember(memberDto);
    }

    // 회원 탈퇴
    @Override
    public int deleteMember(Long memberId) {
        return memberMapper.deleteMember(memberId);
    }

}