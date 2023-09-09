package com.letschat.authentication.controller;

import com.letschat.authentication.bearer.AuthenticationService;
import com.letschat.authentication.dto.AuthenticationRequest;
import com.letschat.authentication.dto.AuthenticationResponse;
import com.letschat.authentication.dto.OnboardRequest;
import com.letschat.authentication.token.VerificationTokenService;
import com.letschat.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/letschat")
public class AuthenticationController {
	
	private final UserService userService;
	private final AuthenticationService authenticationService;
	private final VerificationTokenService verificationTokenService;
	
	@Operation(
		summary = "Onboard a user",
		description = """
            Onboard (signup) a user it will first validate the fields (see "Schemas" for more info)
            if the fields are valid it will send confirmation email to the user
            """
	)
	@ApiResponse(description = "The request was well-formed but was unable to be followed due " +
	"to duplications",
	headers = @Header(name = MediaType.APPLICATION_PROBLEM_JSON_VALUE),
	responseCode = "422",
	content = @Content(
		examples = @ExampleObject(
			name = "UNPROCESSABLE_ENTITY_RESPONSE_BODY",
			value = """
					{
					     "firstName" : "Field required",
					     "lastName" : "Field required",
					     "username" : "Username is taken",
					     "email" : "Account with this email already exists, verify your email, if the request was expired we've sent you a new one",
					     "password" : "Password must contain characters a digit and a symbol"
					}
					"""
			)
		)
	)
	@ApiResponse(description = "Request successfully processed",
		headers = @Header(name = MediaType.APPLICATION_JSON_VALUE),
		responseCode = "200"
	)
	@PostMapping("/onboard")
	public void onboard(@RequestBody @Valid OnboardRequest onboardRequest) {
		userService.onBoardUser(onboardRequest);
	}
	
	@Operation(summary = "Login the user with username and password",
	description = "After the successful login, response will be a bearer token that will be used to " +
		"authenticate the user, the bearer token will last one day before expiration")
	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest request) {
		return ResponseEntity.ok(authenticationService.authenticate(request));
	}
	
	@Operation(hidden = true)
	@GetMapping("/verify")
	public String verifyToken(@RequestParam("token") String token) {
		return verificationTokenService.verifyToken(token);
	}
	
}
