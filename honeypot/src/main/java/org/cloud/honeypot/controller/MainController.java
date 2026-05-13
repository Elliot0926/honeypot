package org.cloud.honeypot.controller;
 
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloud.honeypot.dto.AptComplexDto;
import org.cloud.honeypot.dto.MemberDto;
import org.cloud.honeypot.dto.RentTransactionDto;
import org.cloud.honeypot.dto.SaleTransactionDto;
import org.cloud.honeypot.mapper.AptComplexMapper;
import org.cloud.honeypot.mapper.RentTransactionMapper;
import org.cloud.honeypot.mapper.SaleTransactionMapper;
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
    private SaleTransactionMapper saleTransactionMapper;
 
    @Autowired
    private RentTransactionMapper rentTransactionMapper;
 
    @Autowired
    private AptComplexMapper aptComplexMapper;
 
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
    public List<SaleTransactionDto> getSaleList(
            @RequestParam Long complexId) {
        return saleTransactionMapper.selectByComplex(complexId);
    }
 
    // 단지별 전월세 실거래가 조회 (JSON)
    @GetMapping("/api/rent")
    @ResponseBody
    public List<RentTransactionDto> getRentList(
            @RequestParam Long complexId) {
        return rentTransactionMapper.selectByComplex(complexId);
    }
 
    // 최근 거래현황 - 매매+전월세 합산 최신순 50건
    @GetMapping("/api/recent-trades")
    @ResponseBody
    public List<Map<String, Object>> getRecentTrades() {
        List<Map<String, Object>> result = new ArrayList<>();
 
        // 매매 최근 30건
        List<SaleTransactionDto> sales = saleTransactionMapper.selectRecent(30);
        for (SaleTransactionDto s : sales) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "매매");
            item.put("complexId", s.getComplexId());
            item.put("complexName", s.getComplexName());
            item.put("legalDong", s.getLegalDong());
            item.put("area", s.getArea());
            item.put("floor", s.getFloor());
            item.put("dealYear", s.getDealYear());
            item.put("dealMonth", s.getDealMonth());
            item.put("dealDay", s.getDealDay());
            item.put("price", s.getDealAmount());
            item.put("deposit", null);
            item.put("monthlyRent", null);
            result.add(item);
        }
 
        // 전월세 최근 30건
        List<RentTransactionDto> rents = rentTransactionMapper.selectRecent(30);
        for (RentTransactionDto r : rents) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", r.getRentType() != null ? r.getRentType() : "전세");
            item.put("complexId", r.getComplexId());
            item.put("complexName", r.getComplexName());
            item.put("legalDong", r.getLegalDong());
            item.put("area", r.getArea());
            item.put("floor", r.getFloor());
            item.put("dealYear", r.getDealYear());
            item.put("dealMonth", r.getDealMonth());
            item.put("dealDay", r.getDealDay());
            item.put("price", null);
            item.put("deposit", r.getDeposit());
            item.put("monthlyRent", r.getMonthlyRent());
            result.add(item);
        }
 
        // 날짜 내림차순 정렬
        result.sort(Comparator.<Map<String, Object>>comparingInt(m -> -(Integer) m.get("dealYear"))
            .thenComparingInt(m -> -(Integer) m.get("dealMonth"))
            .thenComparingInt(m -> -(Integer) m.get("dealDay")));
 
        return result.subList(0, Math.min(50, result.size()));
    }
}