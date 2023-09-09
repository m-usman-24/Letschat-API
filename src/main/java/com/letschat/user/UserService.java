package com.letschat.user;

import com.letschat.authentication.dto.OnboardRequest;
import com.letschat.authentication.email.EmailSender;
import com.letschat.authentication.mapper.AuthenticationMappers;
import com.letschat.chat.service.ImageCompressionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Objects;


@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailSender emailSender;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository
			.findById(username)
			.orElseThrow(() -> new UsernameNotFoundException("User with this name not found while loading user in " +
				"UserService class"));
	}
	
	public void onBoardUser(OnboardRequest onboardRequest) {
		
		User userToSave = AuthenticationMappers.INSTANCE.onboardRequestToUser(onboardRequest);
		
		userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
		userToSave.setRole(Role.USER);
		userToSave.setLastOnline(Instant.now());
		
		User savedUser = userRepository.save(userToSave);
		
		String url = emailSender.createEmailVerificationUrl(savedUser);

//		Sending Confirmation Email
		emailSender.sendEmail(
			onboardRequest.email(),
			onboardRequest.firstName() + " " + onboardRequest.lastName(),
			"Verify Your Email",
			url
		);
	}
	
	public void enableUser(String username) {
		
		User user = userRepository
			.findById(username)
			.orElseThrow(() -> new IllegalStateException("User not found while enabling account"));
		
		user.setEnabled(true);
		user.setAccountLocked(false);
	}
	
	@Transactional
	public void changePassword(String username, String password) {
		userRepository.updatePassword(username, passwordEncoder.encode(password));
	}
	
	@Transactional
	public void changeEmail(String email, String username) {
		userRepository.updateEmail(email, username);
	}
	
	@SneakyThrows
	@Transactional
	public void changeProfilePicture(String username, MultipartFile image) {
		
		switch (Objects.requireNonNull(image.getContentType())) {
			
			case "image/png", "image/jpg", "image/jpeg", "image/heic", "image/raw" -> {
				
				byte[] compressedProfilePicture = ImageCompressionService
					.compressImage(image.getBytes(),1.0f);
				
				userRepository.updateProfilePicture(compressedProfilePicture, username);
			}
			default -> throw new InvalidContentTypeException("Invalid image file");
		}
	}
	
	public byte[] fetchProfilePicture(String username) {
		return userRepository.getProfilePicture(username);
		
	}
}
