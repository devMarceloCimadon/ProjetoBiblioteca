package com.marcelo.Biblioteca.model;

import lombok.Data;

@Data
public class Pessoa {
    @Data
    public static class Endereco{
        private int id;
        private int cep;
        private String pais;
        private String logradouro;
        private int numero;
        private String complemento;
    }

    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String celular;
    private String telefone;
    private int idEndereco;
    private Endereco endereco;

}
