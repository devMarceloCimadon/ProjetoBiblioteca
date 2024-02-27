package com.marcelo.Biblioteca.controller;

import com.marcelo.Biblioteca.dao.PessoaDao;
import com.marcelo.Biblioteca.dto.PessoaDTO;
import com.marcelo.Biblioteca.model.Pessoa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PessoaController {

    private final PessoaDao pessoaDao;

    @Autowired
    public PessoaController(PessoaDao pessoaDao) {
        this.pessoaDao = pessoaDao;
    }

    @PostMapping("/pessoa")
    public ResponseEntity<Pessoa> cadastrarPessoa(@RequestBody PessoaDTO pessoaDto) {
        try {
            Pessoa pessoaCadastrada = pessoaDao.cadastrarPessoa(pessoaDto);
            return new ResponseEntity<>(pessoaCadastrada, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/pessoa/{id}")
    public ResponseEntity<Pessoa> atualizarCadastro(@PathVariable int id, @RequestBody Pessoa pessoa){
        try{
            Pessoa.Endereco endereco = pessoa.getEndereco();
            Pessoa pessoaAtualizada = pessoaDao.atualizarPessoa(id, pessoa.getNome(), pessoa.getEmail(), pessoa.getCelular(), pessoa.getTelefone(), endereco);
            return new ResponseEntity<>(pessoaAtualizada, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pessoa/{id}")
    public ResponseEntity<Pessoa> obterPessoaPorID(@PathVariable int id){
        try{
            Pessoa pessoa = pessoaDao.obterPessoaPorID(id);
            if (pessoa != null) {
                return new ResponseEntity<>(pessoa, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pessoa")
    public List<Pessoa> listarPessoas(){
        try {
            List<Pessoa> pessoas = pessoaDao.listarPessoas();
            return pessoas;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Falha ao listar pessoas.", e);
        }
    }

    @DeleteMapping("/pessoa/{id}")
    public ResponseEntity<Void> deletarPessoa(@PathVariable int id){
        try{
            Pessoa pessoaExcluida = pessoaDao.deletarPessoa(id);

            if(pessoaExcluida != null) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                System.out.println("Registro não encontrado.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
