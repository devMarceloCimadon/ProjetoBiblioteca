package com.marcelo.Biblioteca.controller;

import com.marcelo.Biblioteca.dao.EmprestimoDao;
import com.marcelo.Biblioteca.dto.EmprestimoDTO;
import com.marcelo.Biblioteca.model.Emprestimo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
public class EmprestimoController {

    private final EmprestimoDao emprestimoDao;
    @Autowired
    public EmprestimoController(EmprestimoDao emprestimoDao){
        this.emprestimoDao = emprestimoDao;
    }

    @PostMapping("/emprestimo")
    public ResponseEntity<Emprestimo> cadastrarEmprestimo(@RequestBody EmprestimoDTO emprestimoDto){
        try{
            Emprestimo emprestimoCadastrado = emprestimoDao.cadastrarEmprestimo(emprestimoDto);
            return new ResponseEntity<>(emprestimoCadastrado, HttpStatus.CREATED);
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/emprestimo/{id}")
    public ResponseEntity<Emprestimo> atualizarEmprestimo(@PathVariable int id, @RequestBody Emprestimo emprestimo){
        try{
            Date dataDevolucao = new java.sql.Date(emprestimo.getDataDevolucao().getTime());
            Emprestimo emprestimoAtualizado = emprestimoDao.atualizarEmprestimo(id, dataDevolucao);
            if(emprestimoAtualizado != null) {
                return new ResponseEntity<>(emprestimoAtualizado, HttpStatus.OK);
            } else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/emprestimo/{id}")
    public ResponseEntity<Emprestimo> obterEmprestimoPorID(@PathVariable int id){
        try{
            Emprestimo emprestimo = emprestimoDao.obterEmprestimoPorID(id);
            if (emprestimo != null){
                return new ResponseEntity<>(emprestimo, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/emprestimo")
    public List<Emprestimo> listarEmprestimos(){
        try{
            List<Emprestimo> emprestimos = emprestimoDao.listarEmprestimos();
            return emprestimos;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Falha ao listar livros.", e);
        }
    }

    @DeleteMapping("/emprestimo/{id}")
    public ResponseEntity<Void> deletarEmprestimo(@PathVariable int id){
        try{
            Emprestimo emprestimoDeletado = emprestimoDao.deletarEmprestimo(id);
            if (emprestimoDeletado != null){
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                System.out.println("Registro não encontrado.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
