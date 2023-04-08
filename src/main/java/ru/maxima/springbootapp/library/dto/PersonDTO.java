package ru.maxima.springbootapp.library.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class PersonDTO {
    private Long id;
    @NotEmpty(message = "Name shouldn't be empty")
    private String name;
    private Integer age;
    private String email;
    private String phoneNumber;

}
