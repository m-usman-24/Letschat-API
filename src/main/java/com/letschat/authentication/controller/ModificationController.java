package com.letschat.authentication.controller;

import com.letschat.authentication.dto.EmailChangeRequest;
import com.letschat.authentication.dto.PasswordChangeRequest;
import com.letschat.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/letschat")
public class ModificationController {
	
	private final UserService userService;
	
	@Operation(security = @SecurityRequirement(name = "Bearer Auth"),
	summary = "Update the password of the authenticated user",
	description = "Due to technical issues only a authenticated user can update its password that means user can not " +
		"change password using forgot password capabilities for right now, we will add this functionality later")
	@PostMapping("/password")
	public void changePassword(@RequestBody @Valid PasswordChangeRequest passwordChangeRequest,
	                           @AuthenticationPrincipal UserDetails userDetails) {
		userService.changePassword(userDetails.getUsername(), passwordChangeRequest.password());
	}
	
	@Operation(security = @SecurityRequirement(name = "Bearer Auth"),
	summary = "Update the email of the authenticated user")
	@PostMapping("/email")
	public void changeEmail(@RequestBody @Valid EmailChangeRequest emailChangeRequest,
	                        @AuthenticationPrincipal UserDetails userDetails) {
		userService.changeEmail(emailChangeRequest.email(), userDetails.getUsername());
	}
	
	@Operation(security = @SecurityRequirement(name = "Bearer Auth"),
	summary = "Update the profile picture of the authenticated user",
	description = "It takes an image attached to form data it must be an image and less than 5Mb")
	@PostMapping("/profile-picture")
	public void changeProfilePhoto(@RequestPart("image") MultipartFile image,
	                               @AuthenticationPrincipal UserDetails userDetails) {
		userService.changeProfilePicture(userDetails.getUsername(), image);
	}
	
	@Operation(security = @SecurityRequirement(name = "Bearer Auth"),
	summary = "Fetch the profile picture of the authenticated user",
	description = "The fetched will be in jpg format followed by username like \"username\".jpg")
	@GetMapping("/profile-picture")
	public ResponseEntity<byte[]> getProfilePicture(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity
			.status(HttpStatus.OK)
			.headers(h -> {
				h.setContentType(MediaType.IMAGE_JPEG);
				h.setContentDisposition(ContentDisposition
					.inline()
					.filename(userDetails.getUsername() + ".jpg")
					.build()
				);
			})
			.body(userService.fetchProfilePicture(userDetails.getUsername()));
	}
	
}
