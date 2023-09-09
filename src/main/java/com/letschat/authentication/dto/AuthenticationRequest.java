package com.letschat.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
	@Schema(description = "Username of the user", example = "_us.m.an_")
	@NotBlank
	String username,
	@Schema(description = "Password of the user", example = "usman.3344")
	@NotBlank
	String password
) {}
