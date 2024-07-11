package code;

// Copyright (c) 2024, NoCodeNoLife-cloud. All rights reserved.
// Author: NoCodeNoLife-cloud
// stay hungry，stay foolish
import code.entity.Students;
import code.service.impl.StudentsServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
// @EnableEurekaServer
// @EnableDiscoveryClient
// @EnableFeignClients
// @EnableDubbo
@SpringBootApplication
public class SpringBootApplicationBootStrap {
	/**
	 * Entry point of the application.
	 * @param args The command line arguments.
	 */
	@SneakyThrows
	public static void main(String[] args) {
		// Run the Spring Boot application
		SpringApplication.run(SpringBootApplicationBootStrap.class, args);
	}

	/**
	 * This method creates and returns an ApplicationRunner bean.
	 * The ApplicationRunner bean is used to execute code when the application starts.
	 * @return an ApplicationRunner bean
	 */
	@Bean
	public ApplicationRunner applicationRunner(@Autowired StudentsServiceImpl studentsService) {
		return args -> {
			studentsService.save(new Students(1, "张山", 18));
			log.info(studentsService.getById(1).toString());
			studentsService.removeById(1);
		};
	}
}