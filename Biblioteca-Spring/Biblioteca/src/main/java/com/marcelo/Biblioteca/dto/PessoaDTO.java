package com.marcelo.Biblioteca.dto;

import com.marcelo.Biblioteca.model.Pessoa;
import lombok.Data;

@Data
public class PessoaDTO {
    private Pessoa pessoa;
    private Pessoa.Endereco endereco;
}
