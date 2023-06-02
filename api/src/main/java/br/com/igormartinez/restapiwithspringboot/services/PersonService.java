package br.com.igormartinez.restapiwithspringboot.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.igormartinez.restapiwithspringboot.controllers.PersonController;
import br.com.igormartinez.restapiwithspringboot.data.vo.v1.PersonVO;
import br.com.igormartinez.restapiwithspringboot.exceptions.RequiredObjectIsNullException;
import br.com.igormartinez.restapiwithspringboot.exceptions.ResourceNotFoundException;
import br.com.igormartinez.restapiwithspringboot.mapper.DozerMapper;
import br.com.igormartinez.restapiwithspringboot.model.Person;
import br.com.igormartinez.restapiwithspringboot.repositories.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonService {

    private Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
        logger.info("Finding all people");

        Page<Person> personPage = repository.findAll(pageable);

        Page<PersonVO> personVOPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));

        personVOPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(personVOPage, link);
    }

    public PagedModel<EntityModel<PersonVO>> findByFirstName(String firstName, Pageable pageable) {
        logger.info("Finding persons by first name");

        Page<Person> personPage = repository.findByFirstName(firstName, pageable);

        Page<PersonVO> personVOPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));

        personVOPage.map(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class).findByFirstName(firstName, pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(personVOPage, link);
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one PersonVO");
        Person person = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        PersonVO personVO = DozerMapper.parseObject(person, PersonVO.class);
        personVO.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return personVO;
    }

    public PersonVO create(PersonVO personVO) {
        if (personVO == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one PersonVO");
        Person person = DozerMapper.parseObject(personVO, Person.class);
        Person createdPerson = repository.save(person);
        PersonVO createdPersonVO = DozerMapper.parseObject(createdPerson, PersonVO.class); 
        createdPersonVO.add(linkTo(methodOn(PersonController.class).findById(createdPersonVO.getKey())).withSelfRel());
        return createdPersonVO;
    }

    public PersonVO update(PersonVO personVO) {
        if (personVO == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one PersonVO");

        Person person = repository.findById(personVO.getKey())
            .orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        person.setFirstName(personVO.getFirstName());
        person.setLastName(personVO.getLastName());
        person.setAddress(personVO.getAddress());
        person.setGender(personVO.getGender());

        Person updatedPerson = repository.save(person);
        PersonVO updatedPersonVO = DozerMapper.parseObject(updatedPerson, PersonVO.class);
        updatedPersonVO.add(linkTo(methodOn(PersonController.class).findById(updatedPersonVO.getKey())).withSelfRel());
        return updatedPersonVO;
    }

    @Transactional
    public PersonVO disablePerson(Long id) {
        logger.info("Disabling one person");

        repository.disablePerson(id);

        Person person = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        PersonVO personVO = DozerMapper.parseObject(person, PersonVO.class);
        personVO.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
        return personVO;
    }

    public void delete(Long id) {
        logger.info("Deleting one PersonVO");

        Person entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        repository.delete(entity);
    }
}
