package org.cloud.honeypot.service;

/**
 * 데이터 수집 서비스 인터페이스
 * 국토부 API 호출 및 DB 저장 기능 정의
 */
public interface CollectService {

    // 공동주택 단지 정보 수집 (국토부 매매 실거래가 API 활용)
    int collectAptComplex(String sigunguCd) throws Exception;

    // 주소 → 위경도 좌표 변환 (카카오 로컬 API 활용)
    int convertGeoLocation() throws Exception;

    // 매매 실거래가 수집 (국토부 RTMSDataSvcAptTradeDev API)
    int collectSaleTransaction(String sigunguCd, String dealYmd) throws Exception;

    // 전월세 실거래가 수집 (국토부 RTMSDataSvcAptRent API)
    int collectRentTransaction(String sigunguCd, String dealYmd) throws Exception;
}