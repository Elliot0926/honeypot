package org.cloud.honeypot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.cloud.honeypot.dto.FavoriteDto;
import java.util.List;

@Mapper
public interface FavoriteMapper {

    // 즐겨찾기 추가
    int insertFavorite(@Param("memberId") Long memberId, 
                       @Param("complexId") Long complexId);

    // 즐겨찾기 삭제
    int deleteFavorite(@Param("memberId") Long memberId, 
                       @Param("complexId") Long complexId);

    // 즐겨찾기 여부 확인
    int countFavorite(@Param("memberId") Long memberId, 
                      @Param("complexId") Long complexId);

    // 회원 즐겨찾기 목록
    List<FavoriteDto> selectFavoritesByMember(@Param("memberId") Long memberId);
}