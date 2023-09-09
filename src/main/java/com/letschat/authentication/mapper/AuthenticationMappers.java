package com.letschat.authentication.mapper;

import com.letschat.authentication.dto.OnboardRequest;
import com.letschat.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthenticationMappers {

	AuthenticationMappers INSTANCE = Mappers.getMapper(AuthenticationMappers.class);
	
	OnboardRequest userToOnboardRequest(User user);
	
	@Mapping(target = "role", ignore = true)
	@Mapping(target = "enabled", ignore = true)
	@Mapping(target = "credentialsExpired", ignore = true)
	@Mapping(target = "accountLocked", ignore = true)
	@Mapping(target = "accountExpired", ignore = true)
	User onboardRequestToUser(OnboardRequest onboardRequest);
}
