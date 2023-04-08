package ru.maxima.springbootapp.library.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class BookDTO {
    private Long id;
    @NotEmpty(message = "Title shouldn't be empty")
    private String name;
    private Integer yearOfProduction;
    @NotEmpty(message = "Author shouldn't be empty")
    private String author;
    private String annotation;
    private Long personId;

}
