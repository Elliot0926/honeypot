package org.cloud.honeypot.dto;

import lombok.Data;

/**
 * 매매 실거래가 DTO
 * sale_transaction 테이블과 매핑
 */
@Data
public class SaleTransactionDto {

    private Long saleId;          // 거래 고유번호 (PK)
    private Long complexId;       // 단지 고유번호 (FK → apt_complex)
    private String aptDong;       // 아파트 동
    private Integer dealAmount;   // 거래금액 (만원)
    private Double area;          // 전용면적 (㎡)
    private Integer floor;        // 층
    private Integer dealYear;     // 거래년도
    private Integer dealMonth;    // 거래월
    private Integer dealDay;      // 거래일
    private String dealType;      // 거래유형 (중개거래, 직거래 등)
    private String slerGbn;       // 매도자 구분 (개인, 법인 등)
    private String buyerGbn;      // 매수자 구분 (개인, 법인 등)
    private String regDate;       // 등기일자
    private String createdAt;     // DB 등록일시
}
