package org.cloud.honeypot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.cloud.honeypot.dto.SaleTransactionDto;
import java.util.List;

/**
 * 매매 실거래가 MyBatis Mapper 인터페이스
 * SaleTransactionMapper.xml 과 매핑
 */
@Mapper
public interface SaleTransactionMapper {

    // 매매 실거래가 저장
    int insertSale(SaleTransactionDto dto);

    // 중복 체크 (단지ID + 거래일 + 거래금액)
    int countByComplexAndDate(
        @Param("complexId") Long complexId,
        @Param("dealYear") int dealYear,
        @Param("dealMonth") int dealMonth,
        @Param("dealDay") int dealDay,
        @Param("dealAmount") int dealAmount
    );

    // 단지별 매매 거래 목록 조회 (최신순)
    List<SaleTransactionDto> selectByComplex(@Param("complexId") Long complexId);
}
