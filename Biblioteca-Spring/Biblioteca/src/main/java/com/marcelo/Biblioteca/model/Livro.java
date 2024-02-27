package com.marcelo.Biblioteca.model;

import lombok.Data;

@Data
public class Livro {
    private int id;
    private String nome;
    private String autor;
    private String editora;
    private String categoria;
    private boolean disponivel;
}
