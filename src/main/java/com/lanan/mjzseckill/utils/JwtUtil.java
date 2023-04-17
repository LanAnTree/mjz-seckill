package com.lanan.mjzseckill.utils;

import com.lanan.mjzseckill.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author Lucky LanAn
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @createTime 2023/4/10-16:01
 */
@Slf4j
public class JwtUtil {
	/**
	 * 进行数字签名的私钥
	 **/
	private static final String SECRET_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9";

	/**
	 * token过期时间
	 **/
	public static final long EXPIRE = 1000 * 60 * 60 * 24;

	/**
	 * @Description {生成JWT字符串}
	 * @Date 2023/4/10 16:19
	 * @param id
	 * @param expire
	 * @param user
	 * @Return {@link String}
	 */
	public static String getJwtToken(String id, Long expire, User user)  {
		long currentTimeMillis = System.currentTimeMillis();
		//生成jwt令牌
		JwtBuilder jwtBuilder = Jwts.builder()
				// 设置header
				.setHeaderParam("typ", "JWT")
				.setHeaderParam("alg", "HS256")

				// 作为一次性token,从而回避重放攻击
				.setId(id)

				// 设置jwt主题
				.setSubject("mjz-seckill")
				// 设置jwt签发日期
				.setIssuedAt(new Date(currentTimeMillis))
				//设置jwt的过期时间
				.setExpiration(ObjectUtils.isEmpty(expire) ? new Date(currentTimeMillis + EXPIRE) :
						new Date(currentTimeMillis + expire))

				// 设置token主体部分，存储用户信息
				.claim("nickname", user.getNickname())
				.claim("userId", user.getId())

				.signWith(SignatureAlgorithm.HS256, SECRET_KEY);

		log.info("currentTimeMillis>>>" + currentTimeMillis + ": 生成JWT_TOKEN成功");
		return jwtBuilder.compact();
	}


	/**
	 * @Description {判断token是否存在与有效}
	 * @Date 2023/4/10 16:20
	 * @param jwtToken
	 * @Return {@link boolean}
	 */
	public static boolean checkToken(String jwtToken) {
		if(StringUtils.isEmpty(jwtToken)) {
			return false;
		}

		try {
			Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
		} catch (Exception e) {
			log.error("检查jwtToken<String>：" + jwtToken + "异常", e);
			return false;
		}
		return true;
	}

	/**
	 * @Description {判断token是否存在与有效}
	 * @Date 2023/4/10 16:25
	 * @param request
	 * @Return {@link boolean}
	 */
	public static boolean checkToken(HttpServletRequest request) {
		try {
			String jwtToken = request.getHeader("token");
			if(StringUtils.isEmpty(jwtToken)) {
				return false;
			}
			Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
		} catch (Exception e) {
			log.error("检查jwtToken<HttpServletRequest>：" + request.getHeader("token") + "异常", e);
			return false;
		}
		return true;
	}

	/**
	 * @Description {解析JWT字符串}
	 * @Date 2023/4/10 16:04
	 * @param jwt
	 * @Return {@link Claims}
	 */
	public static Claims parseJwt(String jwt) {
		return Jwts.parser()
				.setSigningKey(SECRET_KEY)
				.parseClaimsJws(jwt)
				.getBody();
	}

	/**
	 * @Description {解析JWT字符串}
	 * @Date 2023/4/10 16:37
	 * @param request
	 * @Return {@link Claims}
	 */
	public static Claims parseJwt(HttpServletRequest request) {
		String token = request.getHeader("token");
		if (StringUtils.isEmpty(token)) {
			return null;
		}
		return Jwts.parser()
				.setSigningKey(SECRET_KEY)
				.parseClaimsJws(token)
				.getBody();
	}


	public static void main(String[] args) throws Exception {
		User jwtVo = new User();
		jwtVo.setNickname("mjz");
		jwtVo.setId(1L);
		Claims claims = parseJwt("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxIiwic3ViIjoibWp6LXNlY2tpbGwiLCJpYXQiOjE2ODExMTkyMTgsImV4cCI6MTY4MTIwNTYxOCwibmlja25hbWUiOiJtanoiLCJ1c2VySWQiOjF9.-NANREWUw4sstBExEmxf5348knnFoK4L-FEqzJGuCaA");
		System.out.println(claims.get("userId"));
	}

}

