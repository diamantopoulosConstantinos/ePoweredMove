package com.kosdiam.epoweredmove.gatewayserver.filter;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class AuthFilter implements GlobalFilter {
	
	final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
	
	@Autowired
	@Lazy
    private WebClient.Builder builder;
	
//	//USER resolves in Eureka server
//	private final WebClient webClient = 
//			this.loadBalancedWebClientBuilder().build();

    private Boolean verified;
    private String authorization;
    private Boolean devMode;
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest  request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
		
		logger.info("Pre-Filter executed");
		 
        String requestPath = exchange.getRequest().getPath().toString();
        logger.debug("Request path = " + requestPath);
        
        org.springframework.http.HttpHeaders headers = request.getHeaders();
        Set<String> headerNames = headers.keySet();
        
        devMode = false;
        
        headerNames.forEach((header) -> {
            logger.info(header + " " + headers.get(header));
            if(header.equals("Authorization") || header.equals("authorization")) {
            	authorization = headers.get(header).get(0);
            }
            if (header.equals("Devmode")) {
            	devMode = Boolean.parseBoolean(headers.get(header).get(0));
            	logger.info("Devmode : inside if clause");
            }
        });
        
        if(!devMode) {
        	logger.info("Devmode : false -- " + devMode);
	        //check for specific ips and hostnames ---------------------//
	        String remoteHost = exchange.getRequest().getHeaders().getFirst("Host");
	        logger.debug("Remote Host : " + remoteHost);
//	        if(!remoteHost.contains("34.30.106.243")) { //!remoteHost.contains("localhost") && 
//	        	exchange.getResponse().getHeaders().add("Content-Type", "application/json");
//	        	exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//	        	byte[] bytes = "Prohibited : Not Allowed Host".getBytes(StandardCharsets.UTF_8);
//	        	DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
//	        	return exchange.getResponse().writeWith(Flux.just(buffer));
//	        }
        }
        
        //check for specific allowed calls ---------------------//
        String apiCalled = exchange.getRequest().getPath().toString();
        logger.debug("API Called : " + apiCalled);
        if(apiCalled.contains("/epoweredmove/user/createUser")  || 
           apiCalled.contains("/epoweredmove/user/verifyToken") ||
           apiCalled.contains("/epoweredmove/poi/all") ||
           apiCalled.contains("/epoweredmove/poi/allWithPlugAvailability") ||
           apiCalled.contains("/epoweredmove/plug/allByChargingStation") ||
           apiCalled.contains("/epoweredmove/plug?") ||
           apiCalled.contains("/hello")) {
           logger.info("Allowed API : " + apiCalled);
           exchange.getResponse().setStatusCode(HttpStatus.OK);
           return chain.filter(exchange);
        }
        
        //--------                                                  //
        if(request.getMethod().equals("OPTIONS")){
        	exchange.getResponse().setStatusCode(HttpStatus.OK);
            return chain.filter(exchange);
        }
        verified = null;
        try{
        	logger.debug("----- token below -------");
        	logger.debug(authorization);
        	logger.debug("----- token above -------");
        	verified = this.verifyToken(authorization);
        	logger.debug(" --- --- ---");
        	logger.info("verified : " + verified);
        	
        }catch(Exception ex) {
        	ex.printStackTrace();
        }
            
            
        //γιατί την μία φορά έβγαζε 401 και την άλλη 404.--//
//        if(verified == null || !verified){
//        	exchange.getResponse().getHeaders().add("Content-Type", "application/json");
//        	exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        	byte[] bytes = "Prohibited : Unauthorized Call / User".getBytes(StandardCharsets.UTF_8);
//        	DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
//        	return exchange.getResponse().writeWith(Flux.just(buffer));
//        }
        
        return chain.filter(exchange);
	}
	
	public Boolean verifyToken(String token) throws InterruptedException, ExecutionException {
		return builder.build().post().uri("lb://user/epoweredmove/user/verifyToken").header("Authorization", token)
						.retrieve().bodyToMono(Boolean.class).toFuture().get();
	}
	

}
