package org.Zewang.myBlog.util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: 工具类，用于生成JWT令牌
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/28 18:03
 */

// claims：Claims对象，用于存储令牌中的信息。 是JWT令牌的“载荷”部分，包含关于实体和其他数据的声明
// JWT标准定义了声明为键值对结构。 JWT令牌中的声明可以包含用户信息、权限信息、元数据等信息。
// UserDetails对象，用于存储用户信息，如用户名、密码、权限、状态信息等信息。

@Component
public class JwtUtil {

    // @Value注解用于从配置文件中读取属性值
    @Value("${jwt.secret:mySecretKey}") // mySecretKey 为默认值，如果配置文件中没有指定密钥，则使用这个默认密钥
    private String secret; // 密钥

    @Value("${jwt.expiration:86400}") // 86400 seconds = 24 hours 默认值
    private Long expiration; // 过期时间

    // 获取密钥
    public SecretKey getSingingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes()); // 创建一个密钥对象，使用指定的密钥字符串创建一个HMAC-SHA256密钥
    }

    // 获取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject); // 从令牌中获取用户名
    }

    // 获取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration); // 从令牌中获取过期时间
    }

    // 获取Claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { // Function<Claims, T> claimsResolver 是一个函数接口，用于处理Claims对象； 参数为：Claims对象，返回值类型为T
        final Claims claims = extractAllClaims(token); // 从令牌中获取所有信息
        return claimsResolver.apply(claims); //  返回 用传入的函数处理声明数据 的结果
    }

    // 获取所有Claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder() // 创建解析器
                .setSigningKey(getSingingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查令牌是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date()); // 判断令牌是否过期
    }

    // 生成令牌
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // 创建Claims对象
        return createToken(claims, userDetails.getUsername()); // 生成令牌
    }

    // 创建令牌
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // 添加Claims  claim：键值对，用于存储令牌中的信息
                .setSubject(subject) // 用户名
                .setIssuedAt(new Date(System.currentTimeMillis())) // 令牌生成时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000)) // 令牌过期时间； *1000：转换为毫秒
                .signWith(getSingingKey()) // 使用密钥对签名
                .compact(); // 创建令牌并返回
    }

    // 验证令牌
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
