package com.marcelo.Biblioteca.model;

import lombok.Data;

import java.util.Date;

@Data
public class Emprestimo {
    private int id;
    private Pessoa idPessoa;
    private Livro idLivro;
    private Pessoa pessoa;
    private Livro livro;
    private Date dataEmprestimo;
    private Date previsaoDevolucao;
    private Date dataDevolucao;
}
