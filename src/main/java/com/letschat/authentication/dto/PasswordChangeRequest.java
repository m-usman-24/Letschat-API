package com.letschat.authentication.dto;

import com.letschat.authentication.validator.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PasswordChangeRequest(
	@Schema(description = "Password of the user", example = "usman.3344",
		pattern = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\[\\]\\{\\}|\\\\:;\"'<>,.?/~`]).*$")
	@NotBlank
	@ValidPassword
	String password
) {
}
