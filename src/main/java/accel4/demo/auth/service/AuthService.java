package accel4.demo.auth.service;


import accel4.demo.auth.dto.request.LoginRequest;
import accel4.demo.auth.dto.request.RegisterRequest;
import accel4.demo.auth.dto.response.LoginResponse;

public interface AuthService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout();
    LoginResponse refreshToken(String token);
}