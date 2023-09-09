package com.letschat.authentication.dto;

import com.letschat.authentication.validator.ValidEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EmailChangeRequest(
	@Schema(description = "Email of the user", example = "usmannadeem2233@gmail.com",
		pattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]" +
			"+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
	@NotBlank
	@ValidEmail
	String email
) {}
