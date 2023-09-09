package com.letschat.authentication.dto;

import com.letschat.authentication.validator.ValidEmail;
import com.letschat.authentication.validator.ValidPassword;
import com.letschat.authentication.validator.ValidUsername;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


public record OnboardRequest (
	
	@Schema(description = "First Name of the user", example = "Muhammad")
	@NotBlank()
	String firstName,
	
	@Schema(description = "Last Name of the user", example = "Usman")
	
	@NotBlank
	String lastName,
	
	@Schema(description = "Username of the user", example = "_us.m.an_", pattern = "^[a-z0-9._]{1,30}$")
	@NotBlank
	@ValidUsername
	String username,
	
	@Schema(description = "Email of the user", example = "usmannadeem2233@gmail.com",
		pattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]" +
			"+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
	@NotBlank
	@ValidEmail
	String email,
	
	@Schema(description = "Password of the user", example = "usman.3344",
		pattern = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\[\\]\\{\\}|\\\\:;\"'<>,.?/~`]).*$")
	@NotBlank
	@ValidPassword
	String password
) {}
