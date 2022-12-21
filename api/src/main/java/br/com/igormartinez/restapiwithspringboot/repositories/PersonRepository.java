package br.com.igormartinez.restapiwithspringboot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.igormartinez.restapiwithspringboot.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {}
