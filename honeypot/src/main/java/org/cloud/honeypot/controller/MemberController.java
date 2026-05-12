package org.cloud.honeypot.controller;

import org.cloud.honeypot.dto.MemberDto;
import org.cloud.honeypot.service.FavoriteService;
import org.cloud.honeypot.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    // 즐겨찾기 서비스
    @Autowired
    private FavoriteService favoriteService;

    // 회원가입 폼
    @GetMapping("/join")
    public String joinForm() {
        return "member/join";
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String join(MemberDto memberDto, Model model) {
        if (memberService.checkEmail(memberDto.getEmail())) {
            model.addAttribute("errorMsg", "이미 사용중인 이메일입니다.");
            return "member/join";
        }
        int result = memberService.join(memberDto);
        if (result > 0) {
            return "redirect:/member/login";
        }
        model.addAttribute("errorMsg", "회원가입 실패. 다시 시도해주세요.");
        return "member/join";
    }

    // 로그인 폼
    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(String email, String password,
                        HttpSession session, Model model) {
        MemberDto member = memberService.login(email, password);
        if (member != null) {
            session.setAttribute("loginMember", member);
            return "redirect:/";
        }
        model.addAttribute("errorMsg", "이메일 또는 비밀번호가 틀렸습니다.");
        return "member/login";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // 마이페이지 - 즐겨찾기 목록 포함
    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/member/login";
        }
        model.addAttribute("member", loginMember);
        // 즐겨찾기 목록 추가
        model.addAttribute("favorites",
            favoriteService.getFavorites(loginMember.getMemberId()));
        return "member/mypage";
    }
}