package org.cloud.honeypot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.cloud.honeypot.dto.RentTransactionDto;
import java.util.List;

/**
 * 전월세 실거래가 MyBatis Mapper 인터페이스
 * RentTransactionMapper.xml 과 매핑
 */
@Mapper
public interface RentTransactionMapper {

    // 전월세 실거래가 저장
    int insertRent(RentTransactionDto dto);

    // 중복 체크 (단지ID + 거래일 + 보증금)
    int countByComplexAndDate(
        @Param("complexId") Long complexId,
        @Param("dealYear") int dealYear,
        @Param("dealMonth") int dealMonth,
        @Param("dealDay") int dealDay,
        @Param("deposit") int deposit
    );

    // 단지별 전월세 거래 목록 조회 (최신순)
    List<RentTransactionDto> selectByComplex(@Param("complexId") Long complexId);
}
