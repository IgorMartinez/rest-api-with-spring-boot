package br.com.igormartinez.restapiwithspringboot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.igormartinez.restapiwithspringboot.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>  {}
