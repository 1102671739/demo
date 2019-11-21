package com.example.common.jwt;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class JwtInfo {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 钥匙
     */
    private String key;

    /**
     * 过期时间
     */
    private Timestamp expire;

}
