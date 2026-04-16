package com.unasp.comandadigital.dto.fornecedor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FornecedorRequest(
    @NotBlank String razaoSocial,
    @NotBlank String cnpj,
    String telefone,
    @Email String email
) {}
