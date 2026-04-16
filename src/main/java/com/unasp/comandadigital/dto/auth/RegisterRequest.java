package com.unasp.comandadigital.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 2, max = 100) String nome,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6) String senha,
    String telefone,
    String endereco
) {}
