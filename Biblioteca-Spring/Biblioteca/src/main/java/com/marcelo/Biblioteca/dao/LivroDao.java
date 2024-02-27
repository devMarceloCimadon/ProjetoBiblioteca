package com.marcelo.Biblioteca.dao;

import com.marcelo.Biblioteca.model.Livro;
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
public class LivroDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SQL_INSERT_LIVRO = "INSERT INTO Livro (NOME, AUTOR, EDITORA, CATEGORIA, DISPONIVEL) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_LIVRO = "UPDATE Livro SET disponivel = ? WHERE id = ?";
    private static final String SQL_SELECT_LIVRO = "SELECT * FROM LIVRO WHERE id = ?";
    private static final String SQL_SELECT_ALL_LIVROS = "SELECT * FROM Livro";
    private static final String SQL_DELETE_LIVRO = "DELETE FROM Livro WHERE id = ?";

    public Livro cadastrarLivro(Livro livro) throws Exception{
        try(Connection con = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_INSERT_LIVRO, Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1, livro.getNome());
            ps.setString(2, livro.getAutor());
            ps.setString(3, livro.getEditora());
            ps.setString(4, livro.getCategoria());
            ps.setBoolean(5, livro.isDisponivel());

            int result = ps.executeUpdate();
            if(result == 1){
                ResultSet tableKeys = ps.getGeneratedKeys();
                if(tableKeys.next()){
                    livro.setId(tableKeys.getInt(1));
                    System.out.println("Livro inserido com sucesso.");
                    return livro;
                }
            }
            throw new Exception("Falha ao cadastrar livro.");
        }
    }

    public Livro atualizarLivro(int id, boolean disponibilidade) throws Exception{
        Livro livroParaAtualizar = obterLivroPorID(id);

        if(livroParaAtualizar != null){
            try(Connection con = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_LIVRO)){
                ps.setBoolean(1, disponibilidade);

                ps.setInt(2, id);

                int result = ps.executeUpdate();
                if(result > 0){
                    System.out.println("Registro atualizado com sucesso.");
                } else {
                    System.out.println("Falha ao atualizar registro");
                    return null;
                }
            }
        } else {
            System.out.println("Cadastro não encontrado.");
        }
        return livroParaAtualizar;
    }

    public Livro obterLivroPorID(int id) throws Exception{
        try(Connection con = jdbcTemplate.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_SELECT_LIVRO)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) {
                    Livro livro = new Livro();
                    livro.setId(rs.getInt("id"));
                    livro.setNome(rs.getString("nome"));
                    livro.setAutor(rs.getString("autor"));
                    livro.setEditora(rs.getString("editora"));
                    livro.setCategoria(rs.getString("categoria"));
                    livro.setDisponivel(rs.getBoolean("disponivel"));

                    return livro;
                }
            }
            return null;
        }
    }

    public List<Livro> listarLivros() throws Exception{
        try(Connection con = jdbcTemplate.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL_LIVROS);
        ResultSet rs = ps.executeQuery()){
            List<Livro> lista = new ArrayList<>();
            while (rs.next()){
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setNome(rs.getString("nome"));
                livro.setAutor(rs.getString("autor"));
                livro.setEditora(rs.getString("editora"));
                livro.setCategoria(rs.getString("categoria"));
                livro.setDisponivel(rs.getBoolean("disponivel"));
                lista.add(livro);
            }
            return lista;
        }
    }

    public Livro deletarLivro(int id) throws Exception{

        Livro livroParaDeletar = obterLivroPorID(id);

        if(livroParaDeletar != null){
            try(Connection con = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_DELETE_LIVRO)){
                ps.setInt(1, id);
                int result = ps.executeUpdate();
                if(result > 0){
                    System.out.println("Registro deletado com sucesso.");
                } else {
                    System.out.println("Falha ao deletar registro.");
                    return null;
                }
            }
        } else {
            System.out.println("Registro não encontrado");
        }
        return livroParaDeletar;
    }

}
