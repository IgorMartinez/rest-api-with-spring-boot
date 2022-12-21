package br.com.igormartinez.restapiwithspringboot.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.igormartinez.restapiwithspringboot.data.vo.v1.PersonVO;
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
        return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class) ;
    }

    public PersonVO findById(Long id) {
        logger.info("Finding one PersonVO");
        Person person = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        return DozerMapper.parseObject(person, PersonVO.class); 
    }

    public PersonVO create(PersonVO personVO) {
        logger.info("Creating one PersonVO");
        Person person = DozerMapper.parseObject(personVO, Person.class);
        Person createdPerson = repository.save(person);
        return DozerMapper.parseObject(createdPerson, PersonVO.class); 
    }

    public PersonVO update(PersonVO personVO) {
        logger.info("Updating one PersonVO");

        Person person = repository.findById(personVO.getId())
            .orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        person.setFirstName(personVO.getFirstName());
        person.setLastName(personVO.getLastName());
        person.setAddress(personVO.getAddress());
        person.setGender(personVO.getGender());

        Person updatedPerson = repository.save(person);
        return DozerMapper.parseObject(updatedPerson, PersonVO.class); 
    }

    public void delete(Long id) {
        logger.info("Deleting one PersonVO");

        Person entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        repository.delete(entity);
    }
}
