package org.cloud.honeypot.controller;

import org.cloud.honeypot.dto.MemberDto;
import org.cloud.honeypot.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

/**
 * 관리자 전용 컨트롤러
 * ADMIN 권한 체크 후 데이터 수집 기능 제공 
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CollectService collectService;

    // 관리자 메인 페이지
    @GetMapping("")
    public String adminMain(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        return "admin/index";
    }

    // 데이터 수집 화면 (GET)
    @GetMapping("/collect")
    public String collectForm(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        return "admin/collect";
    }

    // 공동주택 단지 정보 수집 실행
    @PostMapping("/collect/complex")
    public String collectComplex(@RequestParam String sigunguCd,
                                  HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        try {
            int count = collectService.collectAptComplex(sigunguCd);
            model.addAttribute("msg", "단지 수집 완료: " + count + "건");
        } catch (Exception e) {
            model.addAttribute("msg", "수집 실패: " + e.getMessage());
        }
        return "admin/collect";
    }

    // 주소 → 좌표 변환 실행
    @PostMapping("/collect/geo")
    public String convertGeo(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        try {
            int count = collectService.convertGeoLocation();
            model.addAttribute("msg", "좌표 변환 완료: " + count + "건");
        } catch (Exception e) {
            model.addAttribute("msg", "변환 실패: " + e.getMessage());
        }
        return "admin/collect";
    }

    // 매매 실거래가 수집 실행
    @PostMapping("/collect/sale")
    public String collectSale(@RequestParam String sigunguCd,
                               @RequestParam String dealYmd,
                               HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        try {
            int count = collectService.collectSaleTransaction(sigunguCd, dealYmd);
            model.addAttribute("msg", "매매 실거래가 수집 완료: " + count + "건");
        } catch (Exception e) {
            model.addAttribute("msg", "수집 실패: " + e.getMessage());
        }
        return "admin/collect";
    }

    // 전월세 실거래가 수집 실행
    @PostMapping("/collect/rent")
    public String collectRent(@RequestParam String sigunguCd,
                               @RequestParam String dealYmd,
                               HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        try {
            int count = collectService.collectRentTransaction(sigunguCd, dealYmd);
            model.addAttribute("msg", "전월세 실거래가 수집 완료: " + count + "건");
        } catch (Exception e) {
            model.addAttribute("msg", "수집 실패: " + e.getMessage());
        }
        return "admin/collect";
    }

    // 관리자 여부 체크 (role = ADMIN 인지 확인)
    private boolean isAdmin(HttpSession session) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        return loginMember != null && "ADMIN".equals(loginMember.getRole());
    }
    
    
 // 매매 실거래가 일괄 수집 (2023.01 ~ 2025.05)
    @PostMapping("/collect/sale/all")
    public String collectSaleAll(@RequestParam String sigunguCd,
                                  HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        try {
            int total = 0;
            int[][] periods = {
                {2023,1},{2023,2},{2023,3},{2023,4},{2023,5},{2023,6},
                {2023,7},{2023,8},{2023,9},{2023,10},{2023,11},{2023,12},
                {2024,1},{2024,2},{2024,3},{2024,4},{2024,5},{2024,6},
                {2024,7},{2024,8},{2024,9},{2024,10},{2024,11},{2024,12},
                {2025,1},{2025,2},{2025,3},{2025,4},{2025,5}
            };
            for (int[] p : periods) {
                String dealYmd = String.format("%d%02d", p[0], p[1]);
                total += collectService.collectSaleTransaction(sigunguCd, dealYmd);
            }
            model.addAttribute("msg", "매매 일괄 수집 완료: " + total + "건");
        } catch (Exception e) {
            model.addAttribute("msg", "수집 실패: " + e.getMessage());
        }
        return "admin/collect";
    }

    // 전월세 실거래가 일괄 수집 (2023.01 ~ 2025.05)
    @PostMapping("/collect/rent/all")
    public String collectRentAll(@RequestParam String sigunguCd,
                                  HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/";
        try {
            int total = 0;
            int[][] periods = {
                {2023,1},{2023,2},{2023,3},{2023,4},{2023,5},{2023,6},
                {2023,7},{2023,8},{2023,9},{2023,10},{2023,11},{2023,12},
                {2024,1},{2024,2},{2024,3},{2024,4},{2024,5},{2024,6},
                {2024,7},{2024,8},{2024,9},{2024,10},{2024,11},{2024,12},
                {2025,1},{2025,2},{2025,3},{2025,4},{2025,5}
            };
            for (int[] p : periods) {
                String dealYmd = String.format("%d%02d", p[0], p[1]);
                total += collectService.collectRentTransaction(sigunguCd, dealYmd);
            }
            model.addAttribute("msg", "전월세 일괄 수집 완료: " + total + "건");
        } catch (Exception e) {
            model.addAttribute("msg", "수집 실패: " + e.getMessage());
        }
        return "admin/collect";
    }
    
    
    
    
}
