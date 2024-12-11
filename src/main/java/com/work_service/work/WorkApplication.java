package com.work_service.work;

import com.work_service.work.repository.BookJpaDataRepository;
import com.work_service.work.repository.MemberJpaDataRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class WorkApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(BookJpaDataRepository bookJpaDataRepository) {
		return new TestDataInit(bookJpaDataRepository);
	}
}
