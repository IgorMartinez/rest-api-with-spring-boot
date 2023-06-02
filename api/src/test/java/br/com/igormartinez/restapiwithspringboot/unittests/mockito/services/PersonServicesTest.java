package br.com.igormartinez.restapiwithspringboot.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.igormartinez.restapiwithspringboot.data.vo.v1.PersonVO;
import br.com.igormartinez.restapiwithspringboot.exceptions.RequiredObjectIsNullException;
import br.com.igormartinez.restapiwithspringboot.model.Person;
import br.com.igormartinez.restapiwithspringboot.repositories.PersonRepository;
import br.com.igormartinez.restapiwithspringboot.services.PersonService;
import br.com.igormartinez.restapiwithspringboot.unittests.mapper.mocks.MockPerson;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class PersonServicesTest {
    
    MockPerson input;

    @InjectMocks
    private PersonService service;

    @Mock
    PersonRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById(){
        Person person = input.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(person));

        PersonVO personVO = service.findById(1L);
        assertNotNull(personVO);
        assertNotNull(personVO.getKey());
        assertNotNull(personVO.getLinks());
        assertTrue(personVO.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", personVO.getAddress());
        assertEquals("First Name Test1", personVO.getFirstName());
        assertEquals("Female", personVO.getGender());
        assertEquals("Last Name Test1", personVO.getLastName());
    }

    @Test
    void testCreate(){
        Person person = input.mockEntity(1);
        person.setId(null);

        Person persistedPerson = person;
        person.setId(1L);

        when(repository.save(person)).thenReturn(persistedPerson);

        PersonVO personVO = input.mockVO(1);
        PersonVO createdPersonVO = service.create(personVO);
        assertNotNull(createdPersonVO);
        assertNotNull(createdPersonVO.getKey());
        assertNotNull(createdPersonVO.getLinks());
        assertTrue(createdPersonVO.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", createdPersonVO.getAddress());
        assertEquals("First Name Test1", createdPersonVO.getFirstName());
        assertEquals("Female", createdPersonVO.getGender());
        assertEquals("Last Name Test1", createdPersonVO.getLastName());
    }

    @Test
    void testCreateWithNullPersonVO(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdate(){
        Person person = input.mockEntity(1);
        person.setId(1L);

        Person persistedPerson = person;

        when(repository.findById(1L)).thenReturn(Optional.of(person));
        when(repository.save(person)).thenReturn(persistedPerson);

        PersonVO personVO = input.mockVO(1);
        var createdPersonVO = service.update(personVO);
        assertNotNull(createdPersonVO);
        assertNotNull(createdPersonVO.getKey());
        assertNotNull(createdPersonVO.getLinks());
        assertTrue(createdPersonVO.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", createdPersonVO.getAddress());
        assertEquals("First Name Test1", createdPersonVO.getFirstName());
        assertEquals("Female", createdPersonVO.getGender());
        assertEquals("Last Name Test1", createdPersonVO.getLastName());
    }

    @Test
    void testUpdateWithNullPersonVO(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testDelete(){
        Person person = input.mockEntity(1);

        when(repository.findById(1L)).thenReturn(Optional.of(person));

        service.delete(1L);
    }
}
