package org.cloud.honeypot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.cloud.honeypot.dto.AptComplexDto;

@Mapper
public interface AptComplexMapper {

    // 단지 저장
    int insertComplex(AptComplexDto dto);

    // 중복 체크 (aptSeq로)
    int countByAptSeq(String aptSeq);

    // 전체 목록 조회
    List<AptComplexDto> selectAllComplex();

    // 지도 영역 내 단지 조회 (위경도 범위)
    List<AptComplexDto> selectComplexByBounds(
        @org.apache.ibatis.annotations.Param("swLat") double swLat,
        @org.apache.ibatis.annotations.Param("swLng") double swLng,
        @org.apache.ibatis.annotations.Param("neLat") double neLat,
        @org.apache.ibatis.annotations.Param("neLng") double neLng
    );

    // 좌표 업데이트
    int updateGeoLocation(AptComplexDto dto);

    // 단지명 + 법정동으로 단지 조회 (실거래가 매칭용)
    AptComplexDto selectByNameAndDong(
        @org.apache.ibatis.annotations.Param("complexName") String complexName,
        @org.apache.ibatis.annotations.Param("legalDong") String legalDong
    );
    
 // 단지명 + 법정동 중복 체크
    int countByNameAndDong(
        @org.apache.ibatis.annotations.Param("complexName") String complexName,
        @org.apache.ibatis.annotations.Param("legalDong") String legalDong
    );

}