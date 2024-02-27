package com.marcelo.Biblioteca.dao;
import com.marcelo.Biblioteca.dto.EmprestimoDTO;
import com.marcelo.Biblioteca.model.Emprestimo;
import com.marcelo.Biblioteca.model.Livro;
import com.marcelo.Biblioteca.model.Pessoa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmprestimoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PessoaDao pessoaDao;
    @Autowired
    private LivroDao livroDao;

    private static final String SQL_INSERT_EMPRESTIMO = "INSERT INTO Emprestimo (idPessoa, idLivro, dataEmprestimo, previsaoDevolucao) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_EMPRESTIMO = "UPDATE Emprestimo SET dataDevolucao = ? WHERE id = ?";
    private static final String SQL_SELECT_EMPRESTIMO = "SELECT E.id as emprestimo_id, E.*, P.id as pessoa_id, P.nome as pessoa_nome, P.*, L.id as livro_id, L.nome as livro_nome ,L.* " +
                                                        "FROM EMPRESTIMO E " +
                                                        "JOIN PESSOA P ON E.idPessoa = P.id " +
                                                        "JOIN LIVRO L ON E.idLivro = L.id " +
                                                        "WHERE E.id = ?";
    private static final String SQL_SELECT_ALL_EMPRESTIMOS = "SELECT E.id as emprestimo_id, E.*, P.id as pessoa_id, P.nome as pessoa_nome, P.*, L.id as livro_id, L.nome as livro_nome ,L.* " +
                                                             "FROM EMPRESTIMO E " +
                                                             "JOIN PESSOA P ON E.idPessoa = P.id " +
                                                             "JOIN LIVRO L ON E.idLivro = L.id ";
    private static final String SQL_DELETE_EMPRESTIMO = "DELETE FROM Emprestimo WHERE id = ?";

    public Emprestimo cadastrarEmprestimo(EmprestimoDTO emprestimoDto) throws Exception {
        Pessoa pessoa = emprestimoDto.getPessoa();
        Emprestimo emprestimo = emprestimoDto.getEmprestimo();
        Livro livro = emprestimoDto.getLivro();

        try (Connection con = jdbcTemplate.getDataSource().getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT_EMPRESTIMO, Statement.RETURN_GENERATED_KEYS)) {

            Pessoa pessoaObtida = pessoaDao.obterPessoaPorID(pessoa.getId());
            Livro livroObtido = livroDao.obterLivroPorID(livro.getId());

            if (pessoaObtida == null || livroObtido == null) {
                throw new Exception("Pessoa e/ou livro não encontrados.");
            }

            ps.setInt(1, pessoaObtida.getId());
            ps.setInt(2, livroObtido.getId());
            ps.setDate(3, new java.sql.Date(emprestimo.getDataEmprestimo().getTime()));
            ps.setDate(4, new java.sql.Date(emprestimo.getPrevisaoDevolucao().getTime()));

            int result = ps.executeUpdate();
            if (result == 1) {
                ResultSet tableKeys = ps.getGeneratedKeys();
                if (tableKeys.next()) {
                    emprestimo.setId(tableKeys.getInt(1));
                    livroDao.atualizarLivro(livroObtido.getId(), false);
                    System.out.println("Emprestimo inserido com sucesso. ID: " + emprestimo.getId());
                    return emprestimo;
                }
            }

            throw new Exception("Falha ao cadastrar emprestimo. Resultado inesperado: " + result);
        } catch (SQLException e) {
            throw new Exception("Falha ao executar operação no banco de dados", e);
        }
    }

    public Emprestimo atualizarEmprestimo(int id, java.sql.Date dataDevolucao) throws Exception{
        Emprestimo emprestimoParaAtualizar = obterEmprestimoPorID(id);

        if(emprestimoParaAtualizar != null){
            try(Connection con = jdbcTemplate.getDataSource().getConnection();
            PreparedStatement ps = con.prepareStatement(SQL_UPDATE_EMPRESTIMO)){
                ps.setDate(1, dataDevolucao);

                ps.setInt(2, id);

                int result = ps.executeUpdate();
                if(result > 0){
                    System.out.println("Registro atualizado com sucesso.");
                } else {
                    System.out.println("Falha ao atualizar registro.");
                    return null;
                }
            }
        } else {
            System.out.println("Cadastro não encontrado.");
        }
        return emprestimoParaAtualizar;
    }

    public Emprestimo obterEmprestimoPorID(int id) throws Exception{
        try(Connection con = jdbcTemplate.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_SELECT_EMPRESTIMO)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){

                    Livro livro = new Livro();
                    livro.setId(rs.getInt("livro_id"));
                    livro.setNome(rs.getString("livro_nome"));
                    livro.setAutor(rs.getString("autor"));
                    livro.setEditora(rs.getString("editora"));
                    livro.setCategoria(rs.getString("categoria"));
                    livro.setDisponivel(rs.getBoolean("disponivel"));

                    Pessoa pessoa = new Pessoa();
                    pessoa.setId(rs.getInt("pessoa_id"));
                    pessoa.setNome(rs.getString("pessoa_nome"));
                    pessoa.setCpf(rs.getString("cpf"));
                    pessoa.setEmail(rs.getString("email"));
                    pessoa.setCelular(rs.getString("celular"));
                    pessoa.setTelefone(rs.getString("telefone"));
                    pessoa.setIdEndereco(rs.getInt("endereco_id"));

                    Emprestimo emprestimo = new Emprestimo();
                    emprestimo.setId(rs.getInt("id"));
                    emprestimo.setIdLivro(livro);
                    emprestimo.setIdPessoa(pessoa);
                    emprestimo.setDataEmprestimo(rs.getDate("dataEmprestimo"));
                    emprestimo.setPrevisaoDevolucao(rs.getDate("previsaoDevolucao"));
                    emprestimo.setDataDevolucao(rs.getDate("dataDevolucao"));

                    return emprestimo;
                }
            }
            return null;
        }
    }

    public List<Emprestimo> listarEmprestimos() throws Exception{
        try(Connection con = jdbcTemplate.getDataSource().getConnection();
        PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL_EMPRESTIMOS);
        ResultSet rs = ps.executeQuery()){
            List<Emprestimo> lista = new ArrayList<>();
            while (rs.next()){
                Livro livro = new Livro();
                livro.setId(rs.getInt("id"));
                livro.setNome(rs.getString("nome"));
                livro.setAutor(rs.getString("autor"));
                livro.setEditora(rs.getString("editora"));
                livro.setCategoria(rs.getString("categoria"));
                livro.setDisponivel(rs.getBoolean("disponivel"));

                Pessoa pessoa = new Pessoa();
                pessoa.setId(rs.getInt("id"));
                pessoa.setNome(rs.getString("nome"));
                pessoa.setCpf(rs.getString("cpf"));
                pessoa.setEmail(rs.getString("email"));
                pessoa.setCelular(rs.getString("celular"));
                pessoa.setTelefone(rs.getString("telefone"));
                pessoa.setIdEndereco(rs.getInt("endereco_id"));

                Emprestimo emprestimo = new Emprestimo();
                emprestimo.setId(rs.getInt("id"));
                emprestimo.setIdLivro(livro);
                emprestimo.setIdPessoa(pessoa);
                emprestimo.setDataEmprestimo(rs.getDate("dataEmprestimo"));
                emprestimo.setPrevisaoDevolucao(rs.getDate("previsaoDevolucao"));
                emprestimo.setDataDevolucao(rs.getDate("dataDevolucao"));

                lista.add(emprestimo);
            }
            return lista;
        }
    }

    public Emprestimo deletarEmprestimo(int id) throws Exception{
        Emprestimo emprestimoParaDeletar = obterEmprestimoPorID(id);

        if(emprestimoParaDeletar != null) {
            try (Connection con = jdbcTemplate.getDataSource().getConnection();
                 PreparedStatement ps = con.prepareStatement(SQL_DELETE_EMPRESTIMO)) {
                ps.setInt(1, id);
                int result = ps.executeUpdate();
                if (result > 0) {
                    System.out.println("Registro deletado com sucesso.");
                } else {
                    System.out.println("Falha ao deletar registro.");
                    return null;
                }
            }
        } else {
            System.out.println("Registro não encontrado.");
        }
        return emprestimoParaDeletar;
    }
}
