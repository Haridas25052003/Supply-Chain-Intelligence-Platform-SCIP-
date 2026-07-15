package accel4.demo.supplier.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String location;
    private Double rating;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
