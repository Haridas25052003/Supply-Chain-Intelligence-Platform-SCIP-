package accel4.demo.supplier.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSupplierRequest {

    private String name;

    @Email(message = "Email should be valid")
    private String email;

    @Pattern(regexp = "^[0-9]{10,}$", message = "Phone should be valid")
    private String phone;

    private String location;
    private Double rating;
    private Boolean active;


}
