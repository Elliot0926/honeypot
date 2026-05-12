package org.cloud.honeypot.dto;

import lombok.Data;

@Data
public class CollectHistoryDto {

    private Long historyId;
    private String lawdCd;
    private String dealYmd;
    private String collectType;
    private String status;
    private Integer count;
    private String collectedAt;
    private String message;

}