package com.letschat.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

	private final CustomChannelInterceptor channelInterceptor;
	private TaskScheduler taskScheduler;
	
	@Autowired
	public void setTaskExecutor(@Lazy TaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOrigins("*");
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/letschat")
				.enableSimpleBroker("/topic", "/queue")
				.setHeartbeatValue(new long[] {20000, 15000})
				.setTaskScheduler(taskScheduler);
	}
	
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration
			.interceptors(channelInterceptor)
			.interceptors(new ChannelInterceptor() {
				@Override
				public void afterSendCompletion(@NonNull Message<?> message,
				                                @NonNull MessageChannel channel,
				                                boolean sent,
				                                @Nullable Exception ex) {
					StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
					log.debug("<- Incoming <- " + accessor.getMessageType() + " <-");
				}
			});
	}
	
	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		registration
			.interceptors(new ChannelInterceptor() {
				@Override
				public void afterSendCompletion(@NonNull Message<?> message,
				                                @NonNull MessageChannel channel,
				                                boolean sent,
				                                @Nullable Exception ex) {
					StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
					log.debug("-> Outgoing -> " + accessor.getMessageType() + " ->");
				}
			});
	}
	
	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.setMessageSizeLimit(100 * 1024 * 1024); // 100Mb
		registry.setSendBufferSizeLimit(100 * 1024 * 1024); // 100Mb
		registry.setSendTimeLimit(20000); // 20sec
	}
}
