package com.unasp.comandadigital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String ingrediente, double saldoAtual, double necessario) {
        super(String.format("Estoque insuficiente para '%s'. Disponível: %.4f | Necessário: %.4f",
                ingrediente, saldoAtual, necessario));
    }
}
