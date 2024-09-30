package com.rajsubhod.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rajsubhod.authservice.entities.UserInfo;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoDto{

    private String userId;

    @NotNull(message = "Username cannot be Null")
    private String username;


    private String firstName;

    private String lastName;

    @Email(message = "Email should be valid")
    @NotNull(message = "Email cannot be Null")
//    @UniqueElements(message = "This email is already used")
    private String email;

    @NotNull(message = "Password cannot be Null")
    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @Max(12)
    @Min(10)
    private Long phoneNumber;

}
