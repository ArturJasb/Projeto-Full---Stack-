package com.unasp.comandadigital.dto.usuario;

import com.unasp.comandadigital.entity.enums.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
    @NotBlank String nome,
    @NotBlank @Email String email,
    String senha,
    @NotNull Perfil perfil,
    String telefone,
    String endereco
) {}
