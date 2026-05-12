package org.cloud.honeypot.dto;

import lombok.Data;

/**
 * 전월세 실거래가 DTO
 * rent_transaction 테이블과 매핑
 */
@Data
public class RentTransactionDto {

    private Long rentId;          // 거래 고유번호 (PK)
    private Long complexId;       // 단지 고유번호 (FK → apt_complex)
    private String aptDong;       // 아파트 동
    private String rentType;      // 전월세 구분 (전세/월세)
    private Integer deposit;      // 보증금 (만원)
    private Integer monthlyRent;  // 월세 (만원, 전세는 0)
    private Double area;          // 전용면적 (㎡)
    private Integer floor;        // 층
    private Integer dealYear;     // 거래년도
    private Integer dealMonth;    // 거래월
    private Integer dealDay;      // 거래일
    private String createdAt;     // DB 등록일시
}
