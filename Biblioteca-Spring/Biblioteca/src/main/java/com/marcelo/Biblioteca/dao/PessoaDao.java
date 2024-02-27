package com.marcelo.Biblioteca.dao;

import com.marcelo.Biblioteca.dto.PessoaDTO;
import com.marcelo.Biblioteca.model.Pessoa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PessoaDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SQL_INSERT_PESSOA = "Insert INTO Pessoa (NOME, CPF, EMAIL, CELULAR, TELEFONE, ENDERECO_ID) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_ENDERECO = "INSERT INTO Endereco (CEP, PAIS, LOGRADOURO, NUMERO, COMPLEMENTO) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_PESSOA = "UPDATE Pessoa SET nome = ?, email = ?, celular = ?, telefone = ? WHERE id = ?";
    private static final String SQL_UPDATE_ENDERECO = "UPDATE Endereco SET cep = ?, pais = ?, logradouro = ?, numero = ?, complemento = ? WHERE id = ?";
    private static final String SQL_SELECT_PESSOA = "SELECT * FROM Pessoa WHERE id = ?";
    private static final String SQL_SELECT_ENDERECO = "SELECT * FROM Endereco WHERE id = ?";
    private static final String SQL_SELECT_ALL_PESSOAS = "SELECT * FROM Pessoa";
    private static final String SQL_DELETE_ENDERECO = "DELETE FROM Endereco WHERE id = ?";
    private static final String SQL_DELETE_PESSOA = "DELETE FROM Pessoa WHERE id = ?";

    public Pessoa cadastrarPessoa(PessoaDTO pessoaDto) throws Exception {
        Pessoa pessoa = pessoaDto.getPessoa();
        Pessoa.Endereco endereco = cadastrarEndereco(pessoaDto.getEndereco());

        try (Connection con = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT_PESSOA, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pessoa.getNome());
            ps.setString(2, pessoa.getCpf());
            ps.setString(3, pessoa.getEmail());
            ps.setString(4, pessoa.getCelular());
            ps.setString(5, pessoa.getTelefone());
            ps.setInt(6, endereco.getId());

            int result = ps.executeUpdate();
            if (result == 1) {
                ResultSet tableKeys = ps.getGeneratedKeys();
                if (tableKeys.next()) {
                    pessoa.setId(tableKeys.getInt(1));
                    System.out.println("Pessoa inserida com sucesso.");
                    return pessoa;
                }
            }
            throw new Exception("Falha ao cadastrar pessoa.");
        }
    }

    public Pessoa.Endereco cadastrarEndereco(Pessoa.Endereco endereco) throws Exception {
        try (Connection con = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT_ENDERECO, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, endereco.getCep());
            ps.setString(2, endereco.getPais());
            ps.setString(3, endereco.getLogradouro());
            ps.setInt(4, endereco.getNumero());
            ps.setString(5, endereco.getComplemento());

            int result = ps.executeUpdate();
            if (result == 1) {
                ResultSet tableKeys = ps.getGeneratedKeys();
                if (tableKeys.next()) {
                    endereco.setId(tableKeys.getInt(1));
                    System.out.println("Endereço inserido com sucesso.");
                    return endereco;
                }
            }
            throw new Exception("Falha ao cadastrar endereço.");
        }
    }

    public Pessoa atualizarPessoa(int id, String novoNome, String novoEmail, String novoCelular, String novoTelefone, Pessoa.Endereco novoEndereco) throws Exception{

        Pessoa pessoaParaAtualizar = obterPessoaPorID(id);

        if(pessoaParaAtualizar != null) {
            Pessoa.Endereco enderecoParaAtualizar = pessoaParaAtualizar.getEndereco();

            try (Connection con = jdbcTemplate.getDataSource().getConnection();
                 PreparedStatement psAtualizarPessoa = con.prepareStatement(SQL_UPDATE_PESSOA);
                 PreparedStatement psAtualizarEndereco = con.prepareStatement(SQL_UPDATE_ENDERECO)){
                psAtualizarPessoa.setString(1, novoNome);
                psAtualizarPessoa.setString(2, novoEmail);
                psAtualizarPessoa.setString(3, novoCelular);
                psAtualizarPessoa.setString(4, novoTelefone);
                psAtualizarPessoa.setInt(5, id);

                psAtualizarEndereco.setInt(1, novoEndereco.getCep());
                psAtualizarEndereco.setString(2, novoEndereco.getPais());
                psAtualizarEndereco.setString(3, novoEndereco.getLogradouro());
                psAtualizarEndereco.setInt(4, novoEndereco.getNumero());
                psAtualizarEndereco.setString(5, novoEndereco.getComplemento());
                psAtualizarEndereco.setInt(6, id);

                int resultPessoa = psAtualizarPessoa.executeUpdate();
                int resultEndereco = psAtualizarEndereco.executeUpdate();
                if (resultPessoa > 0 && resultEndereco > 0) {
                    System.out.println("Cadastro atualizado com sucesso.");
                } else {
                    System.out.println("Falha ao atualizar cadastro");
                    return null;
                }
            }
        } else {
            System.out.println("Cadastro nao encontrado.");
        }
        return pessoaParaAtualizar;
    }

    public Pessoa.Endereco obterEnderecoPorID(int id) throws Exception{
        try (Connection con = jdbcTemplate.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_SELECT_ENDERECO)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Pessoa.Endereco endereco = new Pessoa.Endereco();
                    endereco.setId(rs.getInt("id"));
                    endereco.setCep(rs.getInt("cep"));
                    endereco.setPais(rs.getString("pais"));
                    endereco.setLogradouro(rs.getString("logradouro"));
                    endereco.setNumero(rs.getInt("numero"));
                    endereco.setComplemento(rs.getString("complemento"));

                    return endereco;
                }
            }
            return null;
        }
    }
    public Pessoa obterPessoaPorID(int id) throws Exception{
        try (Connection con = jdbcTemplate.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_SELECT_PESSOA)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Pessoa pessoa = new Pessoa();
                    pessoa.setId(rs.getInt("id"));
                    pessoa.setNome(rs.getString("nome"));
                    pessoa.setCpf(rs.getString("cpf"));
                    pessoa.setCelular(rs.getString("celular"));
                    pessoa.setTelefone(rs.getString("telefone"));

                    Pessoa.Endereco endereco = new Pessoa.Endereco();
                    endereco.setId(rs.getInt("id"));

                    pessoa.setEndereco(obterEnderecoPorID(id));

                    return pessoa;
                }
            }
            return null;
        }
    }

    public List<Pessoa> listarPessoas() throws Exception {
        try (Connection con = jdbcTemplate.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL_PESSOAS);
        ResultSet rs = ps.executeQuery()) {
            List<Pessoa> lista = new ArrayList<>();
            while (rs.next()){
                Pessoa pessoa = new Pessoa();
                pessoa.setId(rs.getInt("id"));
                pessoa.setNome(rs.getString("nome"));
                pessoa.setCpf(rs.getString("cpf"));
                pessoa.setEmail(rs.getString("email"));
                pessoa.setCelular(rs.getString("celular"));
                pessoa.setTelefone(rs.getString("telefone"));
                pessoa.setIdEndereco(rs.getInt("endereco_id"));
                pessoa.setEndereco(obterEnderecoPorID(pessoa.getIdEndereco()));

                lista.add(pessoa);
            }
            return lista;
        }
    }

    public Pessoa.Endereco deletarEndereco(int idEndereco) throws Exception {
        Pessoa.Endereco enderecoParaDeletar = obterEnderecoPorID(idEndereco);

        if (enderecoParaDeletar != null) {
            try (Connection con = jdbcTemplate.getDataSource().getConnection();
                 PreparedStatement ps = con.prepareStatement(SQL_DELETE_ENDERECO)) {
                ps.setInt(1, idEndereco);
                int result = ps.executeUpdate();
                if(result > 0) {
                    System.out.println("Endereço deletado com sucesso.");
                } else {
                    System.out.println("Falha ao deletar endereço");
                    return null;
                }
            }
        } else {
            System.out.println("Endereço não encontrado.");
        }
        return enderecoParaDeletar;
    }


    public Pessoa deletarPessoa(int id) throws Exception {
        Pessoa pessoaParaDeletar = obterPessoaPorID(id);
        if (pessoaParaDeletar != null) {
            Pessoa.Endereco enderecoParaDeletar = pessoaParaDeletar.getEndereco();
            try (Connection con = jdbcTemplate.getDataSource().getConnection();
                 PreparedStatement psPessoa = con.prepareStatement(SQL_DELETE_PESSOA)) {
                psPessoa.setInt(1, id);
                int resultPessoa = psPessoa.executeUpdate();
                if (resultPessoa > 0) {
                    Pessoa.Endereco enderecoDeletado = deletarEndereco(enderecoParaDeletar.getId());

                    if (enderecoDeletado != null) {
                        System.out.println("Pessoa e endereço deletados com sucesso.");
                        return pessoaParaDeletar;
                    } else {
                        System.out.println("Falha ao deletar endereço.");
                        return null;
                    }
                } else {
                    System.out.println("Falha ao deletar pessoa.");
                    return null;
                }
            }
        } else {
            System.out.println("Pessoa não encontrada.");
            return null;
        }
    }
}
