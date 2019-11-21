package com.example.common.utils;
import java.sql.Timestamp;


import com.example.common.jwt.JwtInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Instant;
import java.util.Date;

public class JwtTokenUtils {

    private static final SignatureAlgorithm SIGN_TYPE = SignatureAlgorithm.HS256;

    /**
     * Token超时时间
     */
    public static final long EXPIRETIME = 60 * 60 * 12 * 1000;

    /**
     * Header中token的key
     */
    public static final String TOKEN_KEY = "token";

    /**
     * 生成token时，使用的secret
     */
    public static final String TOKEN_SECRET = "#$SecretJwtSN^&^";

    /**
     * 生成token
     * @return
     */
    public static String generateToken(){
        long instantNow = Instant.now().toEpochMilli();
        return Jwts.builder().claim("userId",123456).
                claim("userName", "userName").
                claim("key", TOKEN_SECRET).
                setExpiration(new Date(instantNow + EXPIRETIME)).
                signWith(SIGN_TYPE, TOKEN_SECRET).compact();
    }

    public static JwtInfo getUserInfoFromToken(String token){
        Claims claims = getClaimsFromToken(token);
        JwtInfo jwtInfo = new JwtInfo();
        jwtInfo.setUserId((Integer)claims.get("userId"));
        jwtInfo.setUserName((String) claims.get("userName"));
        jwtInfo.setKey((String) claims.get("key"));
        jwtInfo.setExpire(new Timestamp((Long.parseLong(claims.get("exp").toString()))*1000));
        return jwtInfo;
    }

    /**
     * 获取过期时间
     * @param token
     * @return
     */
    public static Date getExpirFromToken(String token){
        return getClaimsFromToken(token).getExpiration();
    }

    /**
     * 是否过期
     * @param token
     * @return
     */
    public static boolean isExpir(String token){
        return getExpirFromToken(token).before(new Date());
    }

    /**
     * 解析JWT
     */
    private static Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token).getBody();
    }

}
