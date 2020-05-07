package com.upmusic.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

//	@Bean
//	@Profile({"default"})
//	public ConfigurableServletWebServerFactory tomcatCustomizer() {
//		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//		factory.addConnectorCustomizers(connector -> connector.addUpgradeProtocol(new Http2Protocol()));
//		return factory;
//	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {

		config.setApplicationDestinationPrefixes("/app");
		config.enableSimpleBroker("/topic")
				.setTaskScheduler(new DefaultManagedTaskScheduler())
				.setHeartbeatValue(new long[]{10000,0});

	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

//		registry.addEndpoint("/ws").withSockJS();

		registry
			.addEndpoint("/upm-player-websocket")
//			.setHandshakeHandler(new DefaultHandshakeHandler() {
//
//				@SuppressWarnings({ "unchecked", "unused" })
//				public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @SuppressWarnings("rawtypes") Map attributes) throws Exception {
//					if (request instanceof ServletServerHttpRequest) {
//						ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
//						HttpSession session = servletRequest.getServletRequest().getSession();
//						attributes.put("sessionId", session.getId());
//					}
//					return true;
//				}
//			})
				.addInterceptors(new HttpHandshakeInterceptor())
				.setAllowedOrigins("*").withSockJS();
	}

	public static class HttpHandshakeInterceptor implements HandshakeInterceptor {

		@Override
		public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
									   Map attributes) throws Exception {
			if (request instanceof ServletServerHttpRequest) {
				ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
				HttpSession session = servletRequest.getServletRequest().getSession();

				log.debug("HttpSession sessionId : {}", session.getId());

				attributes.put("sessionId", session.getId());
			}
			return true;
		}

		public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
								   Exception ex) {
		}
	}

}