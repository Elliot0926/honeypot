package org.cloud.honeypot.dto;

import lombok.Data;

@Data
public class MemberDto {
    
    private Long memberId;
    private String email;
    private String password;
    private String name;
    private String role;
    private String createdAt;
    
}