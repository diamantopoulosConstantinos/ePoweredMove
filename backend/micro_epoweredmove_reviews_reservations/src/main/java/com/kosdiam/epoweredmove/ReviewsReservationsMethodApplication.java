package com.kosdiam.epoweredmove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;


@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class}
)
@EnableFeignClients
@RefreshScope
@EntityScan("com.kosdiam.epoweredmove.models")
@ComponentScans({ @ComponentScan("com.kosdiam.epoweredmove.controllers")})
public class ReviewsReservationsMethodApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewsReservationsMethodApplication.class, args);
	}
}
