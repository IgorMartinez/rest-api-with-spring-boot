package br.com.igormartinez.restapiwithspringboot.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;
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

import br.com.igormartinez.restapiwithspringboot.data.vo.v1.BookVO;
import br.com.igormartinez.restapiwithspringboot.exceptions.RequiredObjectIsNullException;
import br.com.igormartinez.restapiwithspringboot.exceptions.ResourceNotFoundException;
import br.com.igormartinez.restapiwithspringboot.model.Book;
import br.com.igormartinez.restapiwithspringboot.repositories.BookRepository;
import br.com.igormartinez.restapiwithspringboot.services.BookService;
import br.com.igormartinez.restapiwithspringboot.unittests.mapper.mocks.MockBook;


@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    
    MockBook input;

    @InjectMocks
    BookService service;

    @Mock
    BookRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById(){
        Book book = input.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        BookVO bookVO = service.findById(1L);
        assertNotNull(bookVO);
        assertNotNull(bookVO.getId());
        assertNotNull(bookVO.getAuthor());
        assertNotNull(bookVO.getLaunchDate());
        assertNotNull(bookVO.getPrice());
        assertNotNull(bookVO.getTitle());
        assertEquals(1L, bookVO.getId());
        assertEquals("Author 1", bookVO.getAuthor());
        assertEquals(new Date(1), bookVO.getLaunchDate());
        assertEquals(1D, bookVO.getPrice());
        assertEquals("Title 1", bookVO.getTitle());
        assertTrue(bookVO.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
    }

    @Test
    void testFindByIdWithInvalidId() {
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(1L);
        });

        String expectedMessage = "No record found with this ID";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testCreate(){
        Book book = input.mockEntity(1);
        book.setId(null);
        Book persistedBook = book;
        persistedBook.setId(1L);
        when(repository.save(book)).thenReturn(persistedBook);

        BookVO bookVO = input.mockVO(1);
        BookVO persistedBookVO = service.create(bookVO);
        assertNotNull(persistedBookVO);
        assertNotNull(persistedBookVO.getId());
        assertNotNull(persistedBookVO.getAuthor());
        assertNotNull(persistedBookVO.getLaunchDate());
        assertNotNull(persistedBookVO.getPrice());
        assertNotNull(persistedBookVO.getTitle());
        assertEquals(1L, persistedBookVO.getId());
        assertEquals("Author 1", persistedBookVO.getAuthor());
        assertEquals(new Date(1), persistedBookVO.getLaunchDate());
        assertEquals(1D, persistedBookVO.getPrice());
        assertEquals("Title 1", persistedBookVO.getTitle());
        assertTrue(persistedBookVO.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
    }

    @Test
    void testCreateWithNullBookVO(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdate(){
        Book book = input.mockEntity(1);
        Book persistedBook = book;
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(book)).thenReturn(persistedBook);

        BookVO bookVO = input.mockVO(1);
        BookVO persistedBookVO = service.update(bookVO);
        assertNotNull(persistedBookVO);
        assertNotNull(persistedBookVO.getId());
        assertNotNull(persistedBookVO.getAuthor());
        assertNotNull(persistedBookVO.getLaunchDate());
        assertNotNull(persistedBookVO.getPrice());
        assertNotNull(persistedBookVO.getTitle());
        assertEquals(1L, persistedBookVO.getId());
        assertEquals("Author 1", persistedBookVO.getAuthor());
        assertEquals(new Date(1), persistedBookVO.getLaunchDate());
        assertEquals(1D, persistedBookVO.getPrice());
        assertEquals("Title 1", persistedBookVO.getTitle());
        assertTrue(persistedBookVO.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
    }

    @Test
    void testUpdateWithNullBookVO(){
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });

        String expectedMessage = "It is not allowed to persist a null object!";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testUpdateWithInvalidBook(){
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            BookVO bookVO = input.mockVO(1);
            service.update(bookVO);
        });

        String expectedMessage = "No record found with this ID";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    void testDelete(){ 
        Book book = input.mockEntity(1);
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        service.delete(1L);
    }

    @Test
    void testDeleteWithInvalidId(){ 
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(1L);
        });

        String expectedMessage = "No record found with this ID";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
