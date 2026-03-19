package com.example.todo.service;

import com.example.todo.domain.User;
import com.example.todo.domain.VerificationToken;
import com.example.todo.repository.UserRepository;
import com.example.todo.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SignupService {
    
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public void signup(SignupForm form){

        if (userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다");
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다");
        }

        User user = User.builder()
                .username(form.getUsername())
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .build();
        
        userRepository.save(user);

        String tokenValue = UUID.randomUUID().toString();

        tokenRepository.save(
            VerificationToken.builder()
                .token(tokenValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build()
        );

        sendVerificationEmail(user.getEmail(), tokenValue);
    }
    
    @Transactional
    public void verify(String tokenValue) {

        VerificationToken token = tokenRepository.findByToken(tokenValue)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 토큰입니다"));

        if (token.isExpired()) {
            throw new IllegalStateException("만료된 인증 토큰입니다");
        }

        token.getUser().enabled();

        tokenRepository.delete(token);
    }

    private void sendVerificationEmail(String to, String tokenValue) {
        String link = baseUrl + "/signup/verify?token=" + tokenValue;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("이메일 인증을 완료해주세요");
        message.setText("아래 링크를 클릭하면 인증이 완료됩니다.\n\n" + link
                + "\n\n링크는 30분 동안 유효합니다.");

        mailSender.send(message);
    }
}
