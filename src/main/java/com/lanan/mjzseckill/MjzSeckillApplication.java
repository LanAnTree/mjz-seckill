package com.lanan.mjzseckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description MjzSeckillApplication 启动类
 * @Created with IntelliJ IDEA 2021.3.1 .
 * @Author Lucky LanAn
 * @Date 2023-04-10 11:42:41
 **/
@SpringBootApplication
@MapperScan("com.lanan.mjzseckill.mapper")
public class MjzSeckillApplication {

	public static void main(String[] args) {
		SpringApplication.run(MjzSeckillApplication.class, args);
	}

}
