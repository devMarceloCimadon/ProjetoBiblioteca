package com.marcelo.Biblioteca.dto;

import com.marcelo.Biblioteca.model.Emprestimo;
import com.marcelo.Biblioteca.model.Livro;
import com.marcelo.Biblioteca.model.Pessoa;
import lombok.Data;

@Data
public class EmprestimoDTO {
    private Emprestimo emprestimo;
    private Pessoa pessoa;
    private Livro livro;
}
