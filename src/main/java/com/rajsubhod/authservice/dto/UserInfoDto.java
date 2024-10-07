package com.rajsubhod.authservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rajsubhod.authservice.validation.emailValidator.UniqueEmail;
import com.rajsubhod.authservice.validation.passwordValidator.ValidPassword;
import jakarta.validation.constraints.*;
import lombok.*;


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
    @UniqueEmail(message = "Email already in use")
    private String email;

    @NotNull(message = "Password cannot be Null")
    @NotEmpty(message = "Password cannot be empty")
    @ValidPassword(message = "Password must be 8 characters long and contain atleast one digit and one special character")
    private String password;

    @Max(12)
    @Min(10)
    private Long phoneNumber;

}
