package com.kosdiam.epoweredmove.gatewayserver;

import java.util.ArrayList;
import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;



@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, 
								   ManagementWebSecurityAutoConfiguration.class })
@EnableDiscoveryClient
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Autowired
	private TokenRelayGatewayFilterFactory filterFactory;
	
	 @Bean
	 @LoadBalanced
	 public WebClient.Builder loadBalancedWebClientBuilder() {
	     return WebClient.builder();
	 }

	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
	    return builder.routes().
	        route(p -> p
	            .path("/epoweredmove/chargingStation/**")
//	            .filters(f -> f.filters(filterFactory.apply())
//					.rewritePath("/epoweredmove/chargingStation/(?<segment>.*)","/${segment}")
//					.removeRequestHeader("Cookie"))
	            .uri("lb://POICHARGINGSTATION")).
	        route(p -> p
		            .path("/epoweredmove/poi/**")
//		            .filters(f -> f.filters(filterFactory.apply())
//						.rewritePath("/epoweredmove/poi/(?<segment>.*)","/${segment}")
//						.removeRequestHeader("Cookie"))
		            .uri("lb://POICHARGINGSTATION")).
	        route(p -> p
		            .path("/epoweredmove/vehicle/**")
//		            .filters(f -> f.filters(filterFactory.apply())
//						.rewritePath("/epoweredmove/vehicle/(?<segment>.*)","/${segment}")
//						.removeRequestHeader("Cookie"))
		            .uri("lb://VEHICLE")).
	        route(p -> p
		            .path("/epoweredmove/paymentMethod/**")
//		            .filters(f -> f.filters(filterFactory.apply())
//						.rewritePath("/epoweredmove/paymentMethod/(?<segment>.*)","/${segment}")
//						.removeRequestHeader("Cookie"))
		            .uri("lb://PAYMENTMETHOD")).
	        route(p -> p
		            .path("/epoweredmove/plug/**")
//		            .filters(f -> f.filters(filterFactory.apply())
//						.rewritePath("/epoweredmove/plug/(?<segment>.*)","/${segment}")
//						.removeRequestHeader("Cookie"))
		            .uri("lb://PLUGPLUGTYPE")).
	        route(p -> p
		            .path("/epoweredmove/plugType/**")
//		            .filters(f -> f.filters(filterFactory.apply())
//						.rewritePath("/epoweredmove/plugType/(?<segment>.*)","/${segment}")
//						.removeRequestHeader("Cookie"))
		            .uri("lb://PLUGPLUGTYPE")).
	        route(p -> p
		            .path("/epoweredmove/user/**")
//		            .filters(f -> f.filters(filterFactory.apply())
//						.rewritePath("/epoweredmove/user/(?<segment>.*)","/${segment}")
//						.removeRequestHeader("Cookie"))
		            .uri("lb://USER")).
	        route(p -> p
		            .path("/epoweredmove/review/**")
//		            .filters(f -> f.filters(filterFactory.apply())
//						.rewritePath("/epoweredmove/review/(?<segment>.*)","/${segment}")
//						.removeRequestHeader("Cookie"))
		            .uri("lb://REVIEWSRESERVATIONS")).
	        route(p -> p
		            .path("/epoweredmove/reservation/**")
//		            .filters(f -> f.filters(filterFactory.apply())
//						.rewritePath("/epoweredmove/reservation/(?<segment>.*)","/${segment}")
//						.removeRequestHeader("Cookie"))
		            .uri("lb://REVIEWSRESERVATIONS")).
	        build();
	}
	
	
	  @Autowired
	  RouteDefinitionLocator locator;

	   @Bean
	   public List<GroupedOpenApi> apis() {
	      List<GroupedOpenApi> groups = new ArrayList<>();
	      List<RouteDefinition> definitions = locator
	         .getRouteDefinitions().collectList().block();
	      assert definitions != null;
	      definitions.stream().filter(routeDefinition -> routeDefinition
	         .getId()
	         .matches(".*-service"))
	         .forEach(routeDefinition -> {
	            String name = routeDefinition.getId();
	               //.replaceAll("-service", "");
	            groups.add(GroupedOpenApi.builder()
	               .pathsToMatch("/" + name + "/**").group(name).build());
	         });
	      return groups;
	   }
	
	

}
