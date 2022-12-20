package br.com.igormartinez.restapiwithspringboot.services;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import br.com.igormartinez.restapiwithspringboot.models.Person;

@Service
public class PersonServices {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonServices.class.getName());
    
    public Person findById(String id) {
        logger.info("Finding one person");
        
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Lionel");
        person.setLastName("Messi");
        person.setAddress("Argentina");
        person.setGender("M");
        
        return person;
    }
}
