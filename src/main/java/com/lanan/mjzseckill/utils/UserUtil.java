package com.lanan.mjzseckill.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lanan.mjzseckill.entity.User;
import com.lanan.mjzseckill.vo.ResponseResultVo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生成用户工具类
 *
 * @author: LC
 * @date 2022/3/4 3:29 下午
 * @ClassName: UserUtil
 */
public class UserUtil {

	private static void createUser(int count) throws Exception {
		List<User> users = new ArrayList<>(count);
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		for (int i = 0; i < count; i++) {
			User user = new User();
			user.setId((long) i);
			user.setLoginCount(1);
			user.setNickname("user:" + i);
			user.setRegisterDate(new Date());
			user.setHead("/default");
			user.setSalt("randomSalt");
//			user.setPassword(passwordEncoder.encode("password"));
			user.setPassword("$2a$10$Z98Nr2BWW34apDjy0qm0feN2hUEZ1tI26tMJu4rdjoVhF7F0oYS3e");
			user.setPhone(13000000000L + i);
			users.add(user);
		}
		System.out.println("create user");
		//插入数据库
		Connection conn = getConn();
		String sql = "insert into t_user(id, login_count, nickname, register_date, head, salt, password, phone)values(?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		for (User user : users) {
			pstmt.setLong(1, user.getId());
			pstmt.setInt(2, user.getLoginCount());
			pstmt.setString(3, user.getNickname());
			pstmt.setTimestamp(4, new Timestamp(user.getRegisterDate().getTime()));
			pstmt.setString(5, user.getHead());
			pstmt.setString(6, user.getSalt());
			pstmt.setString(7, user.getPassword());
			pstmt.setLong(8, user.getPhone());
			pstmt.addBatch();
		}
		pstmt.executeBatch();
		pstmt.close();
		conn.close();
		System.out.println("insert to db");
		//登录，生成token
		String urlString = "http://localhost/user/login";
		File file = new File("C:\\Users\\25097\\Desktop\\config.txt");
		if (file.exists()) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			URL url = new URL(urlString);
			HttpURLConnection co = (HttpURLConnection) url.openConnection();
			co.setRequestMethod("POST");
			co.setRequestProperty("Connection", "keep-Alive");
			co.setRequestProperty("Content-Type","application/json");
			co.setDoOutput(true);
			co.setDoInput(true);
			co.connect();
			OutputStream out = co.getOutputStream();
			String params = "{\"nickname\":\"" + user.getNickname() + "\",\"password\":\"" + "password" + "\"}";
			out.write(params.getBytes());
			out.flush();
			InputStream inputStream = co.getInputStream();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buff)) >= 0) {
				bout.write(buff, 0, len);
			}
			inputStream.close();
			bout.close();
			String response = bout.toString();
			ObjectMapper mapper = new ObjectMapper();
			ResponseResultVo respBean = mapper.readValue(response, ResponseResultVo.class);
			String userTicket = respBean.getData().get("token");
			System.out.println("create userTicket : " + user.getId());
			raf.seek(raf.length());
			raf.write(userTicket.getBytes());
			raf.write("\r\n".getBytes());
			System.out.println("write to file : " + user.getId());
		}
		raf.close();
		System.out.println("over");
	}
	private static Connection getConn() throws Exception {
		String url = "jdbc:mysql://localhost:3306/tb_seckill?useUnicode=true&characterEncoding=UTF-8&serverTimeZone=Asia/Shanghai";
		String username = "root";
		String password = "password";
		String driver = "com.mysql.cj.jdbc.Driver";
		Class.forName(driver);
		return DriverManager.getConnection(url, username, password);
	}

	public static void main(String[] args) throws Exception {
		createUser(5000);
	}
}
