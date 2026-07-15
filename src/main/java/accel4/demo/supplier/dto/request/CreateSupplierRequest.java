package accel4.demo.supplier.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSupplierRequest {

    @NotBlank(message = "supplier name is required")
    private String name;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Pattern(regexp = "^[0-9]{10,}$", message = "Phone should be valid")
    private String phone;

    @NotBlank(message = "Location is required")
    private String location;



}
