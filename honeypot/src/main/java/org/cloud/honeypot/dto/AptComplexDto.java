package org.cloud.honeypot.dto;

import lombok.Data;

@Data
public class AptComplexDto {

    private Long complexId;        // 단지 고유번호
    private String complexName;    // 단지명
    private String aptSeq;         // 국토부 단지코드
    private String sigunguCd;      // 시군구코드
    private String legalDong;      // 법정동
    private String umdCd;          // 읍면동코드
    private String roadAddress;    // 도로명주소
    private String jibunAddress;   // 지번주소
    private Double latitude;       // 위도
    private Double longitude;      // 경도
    private String geoStatus;      // 좌표변환상태
    private Integer buildYear;     // 건축년도
    private Integer householdCount;// 세대수
    private Integer dongCount;     // 동수
    private String createdAt;      // 등록일시

}