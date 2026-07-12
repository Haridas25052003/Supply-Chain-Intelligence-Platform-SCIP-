package accel4.demo.auth.service;


import accel4.demo.auth.dto.request.LoginRequest;
import accel4.demo.auth.dto.request.RegisterRequest;
import accel4.demo.auth.dto.response.LoginResponse;
import accel4.demo.auth.entity.User;
import accel4.demo.auth.mapper.UserMapper;
import accel4.demo.auth.repository.UserRepository;
import accel4.demo.exception.ResourceNotFoundException;
import accel4.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists with this email");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        log.info("User registered successfully: {}", request.getEmail());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }

    @Override
    public void logout() {
        // Implement token blacklisting if needed
        log.info("User logged out");
    }

    @Override
    public LoginResponse refreshToken(String token) {
        String email = jwtUtil.extractEmail(token);
        if (!jwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Invalid token");
        }

        String newAccessToken = jwtUtil.generateAccessToken(email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .user(userMapper.toResponse(user))
                .build();
    }
}