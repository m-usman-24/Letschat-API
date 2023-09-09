package com.letschat.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthenticationResponse(
	@Schema(example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJfdXNtYW5fIiwiaWF0IjoxNjkzNjc4MDU2LCJle" +
		"HAiOjE2OTM3NjQ0NTZ9.BjgFgxi3v9XoH6UMtPXHMWZ59xNYcADuhFqmKrnVWGk")
	String bearerToken,
	@Schema(example = "_usman_")
	String username,
	@Schema(example = "Muhammad Usman")
	String fullName
) {}
