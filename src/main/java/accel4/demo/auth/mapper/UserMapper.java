package accel4.demo.auth.mapper;

import accel4.demo.auth.dto.request.RegisterRequest;
import accel4.demo.auth.entity.User;
import accel4.demo.auth.entity.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(UserRole.EMPLOYEE)
                .active(true)
                .build();
    }

    public UserResponse toResponse(User user) {
        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}