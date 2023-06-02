package br.com.igormartinez.restapiwithspringboot.integrationtests.repositories;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.igormartinez.restapiwithspringboot.exceptions.ResourceNotFoundException;
import br.com.igormartinez.restapiwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.igormartinez.restapiwithspringboot.model.Person;
import br.com.igormartinez.restapiwithspringboot.repositories.PersonRepository;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {
    
    @Autowired
    public PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setup() {
        person = new Person();
    }

    @Test
	@Order(0)
	void testFindByFirstName() throws JsonMappingException, JsonProcessingException {
        
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Direction.ASC, "firstName"));
		person = repository.findByFirstName("Lion", pageable).getContent().get(0);

		assertNotNull(person);
		assertNotNull(person.getId());
		assertNotNull(person.getFirstName());
		assertNotNull(person.getLastName());
		assertNotNull(person.getAddress());
		assertNotNull(person.getGender());
		assertNotNull(person.getEnabled());

		assertEquals(1, person.getId());
		assertEquals("Lionel", person.getFirstName());
		assertEquals("Messi", person.getLastName());
		assertEquals("Argentina", person.getAddress());
		assertEquals("M", person.getGender());
		assertTrue(person.getEnabled());
	}

    @Test
	@Order(1)
	void testDisablePerson() throws JsonMappingException, JsonProcessingException {
        
        repository.disablePerson(person.getId());

        person = repository.findById(person.getId())
            .orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));

		assertNotNull(person);
		assertNotNull(person.getId());
		assertNotNull(person.getFirstName());
		assertNotNull(person.getLastName());
		assertNotNull(person.getAddress());
		assertNotNull(person.getGender());
		assertNotNull(person.getEnabled());

		assertEquals(1, person.getId());
		assertEquals("Lionel", person.getFirstName());
		assertEquals("Messi", person.getLastName());
		assertEquals("Argentina", person.getAddress());
		assertEquals("M", person.getGender());
		assertFalse(person.getEnabled());
	}
}
