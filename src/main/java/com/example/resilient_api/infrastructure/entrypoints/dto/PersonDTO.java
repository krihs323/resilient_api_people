package com.example.resilient_api.infrastructure.entrypoints.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class PersonDTO {
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres.")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe proporcionar un formato de email válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    private String email;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 12, message = "La edad mínima debe ser 12 años")
    @Max(value = 100, message = "La edad no puede exceder los 100 años")
    private Integer age;

    @NotEmpty(message = "La lista de bootcamps no puede estar vacía")
    @Size(min = 1, message = "Debe estar asociado al menos a 1 bootcamp")
    private List<PersonBootcampDTO> personBootcampsList;

}
