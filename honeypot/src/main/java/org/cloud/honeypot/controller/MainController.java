package org.cloud.honeypot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloud.honeypot.dto.AptComplexDto;
import org.cloud.honeypot.dto.MemberDto;
import org.cloud.honeypot.mapper.AptComplexMapper;
import org.cloud.honeypot.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;



@Controller
public class MainController {
	
	@Autowired
	private org.cloud.honeypot.mapper.SaleTransactionMapper saleTransactionMapper;

	@Autowired
	private org.cloud.honeypot.mapper.RentTransactionMapper rentTransactionMapper;

    @Autowired
    private AptComplexMapper aptComplexMapper;

    // 즐겨찾기 서비스
    @Autowired
    private FavoriteService favoriteService;

    // 메인 페이지
    @GetMapping("/")
    public String main(HttpSession session, Model model) {
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        if (loginMember != null) {
            model.addAttribute("loginMember", loginMember);
        }
        return "index";
    }

    // 지도 영역 내 단지 목록 반환 (JSON)
    @GetMapping("/api/complexes")
    @ResponseBody
    public List<AptComplexDto> getComplexes(
            @RequestParam double swLat,
            @RequestParam double swLng,
            @RequestParam double neLat,
            @RequestParam double neLng) {
        return aptComplexMapper.selectComplexByBounds(swLat, swLng, neLat, neLng);
    }

    // 즐겨찾기 토글 (추가/삭제)
    @PostMapping("/api/favorite/toggle")
    @ResponseBody
    public Map<String, Object> toggleFavorite(
            @RequestParam Long complexId,
            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        if (loginMember == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }
        boolean added = favoriteService.toggleFavorite(loginMember.getMemberId(), complexId);
        result.put("success", true);
        result.put("added", added);
        return result;
    }

    // 즐겨찾기 여부 확인
    @GetMapping("/api/favorite/check")
    @ResponseBody
    public Map<String, Object> checkFavorite(
            @RequestParam Long complexId,
            HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");
        if (loginMember == null) {
            result.put("isFavorite", false);
            return result;
        }
        result.put("isFavorite", favoriteService.isFavorite(loginMember.getMemberId(), complexId));
        return result;
    }
    
 // 단지별 매매 실거래가 조회 (JSON)
    @GetMapping("/api/sale")
    @ResponseBody
    public List<org.cloud.honeypot.dto.SaleTransactionDto> getSaleList(
            @RequestParam Long complexId) {
        return saleTransactionMapper.selectByComplex(complexId);
    }

    // 단지별 전월세 실거래가 조회 (JSON)
    @GetMapping("/api/rent")
    @ResponseBody
    public List<org.cloud.honeypot.dto.RentTransactionDto> getRentList(
            @RequestParam Long complexId) {
        return rentTransactionMapper.selectByComplex(complexId);
    }
    
}