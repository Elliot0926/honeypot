package org.cloud.honeypot.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FavoriteDto {
    private Long favoriteId;
    private Long memberId;
    private Long complexId;
    private LocalDateTime createdAt;
    
    // 조회용 (join)
    private String complexName;
    private String legalDong;
    private Integer buildYear;
    private Integer householdCount;
    private Double latitude;
    private Double longitude;
}