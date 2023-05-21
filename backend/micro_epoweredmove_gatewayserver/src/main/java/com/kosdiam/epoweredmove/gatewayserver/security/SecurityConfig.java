package com.kosdiam.epoweredmove.gatewayserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges
        				.pathMatchers("/epoweredmove/chargingStation/**").permitAll()
                        .pathMatchers("/epoweredmove/poi/**").permitAll()
                        .pathMatchers("/epoweredmove/vehicle/**").permitAll()
                        .pathMatchers("/epoweredmove/paymentMethod/**").permitAll()
                        .pathMatchers("/epoweredmove/plug/**").permitAll()
                        .pathMatchers("/epoweredmove/plugType/**").permitAll()
                        .pathMatchers("/epoweredmove/user/**").permitAll()
                        .pathMatchers("/epoweredmove/review/**").permitAll()
                        .pathMatchers("/epoweredmove/reservation/**").permitAll()
        		);
        http.csrf().disable();
        return http.build();
    }
}
