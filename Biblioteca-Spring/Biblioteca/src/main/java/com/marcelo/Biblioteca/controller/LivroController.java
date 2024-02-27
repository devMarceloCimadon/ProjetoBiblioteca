package com.marcelo.Biblioteca.controller;

import com.marcelo.Biblioteca.dao.LivroDao;
import com.marcelo.Biblioteca.model.Livro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LivroController {

    private final LivroDao livroDao;
    @Autowired
    public LivroController(LivroDao livroDao){
        this.livroDao = livroDao;
    }

    @PostMapping("/livro")
    public ResponseEntity<Livro> cadastrarLivro(@RequestBody Livro livro){
        try{
            Livro livroCadastrado = livroDao.cadastrarLivro(livro);
            return new ResponseEntity<>(livroCadastrado, HttpStatus.CREATED);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/livro/{id}")
    public ResponseEntity<Livro> atualizarLivro(@PathVariable int id, @RequestBody Livro livro){
        try{
            Livro livroAtualizado = livroDao.atualizarLivro(id, livro.isDisponivel());
            return new ResponseEntity<>(livroAtualizado, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/livro/{id}")
    public ResponseEntity<Livro> obterLivroPorID(@PathVariable int id) {
        try {
            Livro livro = livroDao.obterLivroPorID(id);
            if (livro != null) {
                return new ResponseEntity<>(livro, HttpStatus.OK);
            } else {
                System.out.println("Livro não encontrado.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/livro")
    public List<Livro> listarLivros(){
        try{
            List<Livro> livros = livroDao.listarLivros();
            return livros;
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Falha ao listar livros.", e);
        }
    }

    @DeleteMapping("/livro/{id}")
    public ResponseEntity<Void> deletarLivro(@PathVariable int id){
        try{
            Livro livroDeletado = livroDao.deletarLivro(id);
            if (livroDeletado != null){
                return new ResponseEntity<>(HttpStatus.OK);
            } else{
                System.out.println("Registro não encontrado.");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
