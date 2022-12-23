package br.com.igormartinez.restapiwithspringboot.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class PersonService {

    private Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    public List<PersonVO> findAll() {
        logger.info("Finding all people");
        List<PersonVO> listPersonVO = DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
        listPersonVO.stream().forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
        return listPersonVO;
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

    public void delete(Long id) {
        logger.info("Deleting one PersonVO");

        Person entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        repository.delete(entity);
    }
}
