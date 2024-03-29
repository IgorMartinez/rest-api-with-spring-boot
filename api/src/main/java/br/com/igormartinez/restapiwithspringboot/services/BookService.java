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

import br.com.igormartinez.restapiwithspringboot.repositories.BookRepository;
import br.com.igormartinez.restapiwithspringboot.controllers.BookController;
import br.com.igormartinez.restapiwithspringboot.data.vo.v1.BookVO;
import br.com.igormartinez.restapiwithspringboot.exceptions.RequiredObjectIsNullException;
import br.com.igormartinez.restapiwithspringboot.exceptions.ResourceNotFoundException;
import br.com.igormartinez.restapiwithspringboot.mapper.DozerMapper;
import br.com.igormartinez.restapiwithspringboot.model.Book;

@Service
public class BookService {
    private Logger logger = Logger.getLogger(BookService.class.getName());

    @Autowired
    public BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookVO> assembler;

    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable){
        logger.info("Finding all books");

        Page<Book> bookPage = repository.findAll(pageable);

        Page<BookVO> bookVOPage = bookPage.map(b -> DozerMapper.parseObject(b, BookVO.class));

        bookVOPage.map(b -> b.add(linkTo(methodOn(BookController.class).findById(b.getId())).withSelfRel()));

        Link link = linkTo(methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        
        return assembler.toModel(bookVOPage, link);
    }

    public BookVO findById(Long id){
        logger.info("Finding a book by id");
        Book book = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        BookVO bookVO = DozerMapper.parseObject(book, BookVO.class);
        bookVO.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
        return bookVO;
    }
    
    public BookVO create(BookVO bookVO) {
        logger.info("Create a new book");
        if (bookVO == null) throw new RequiredObjectIsNullException();

        Book book = DozerMapper.parseObject(bookVO, Book.class);
        Book createdBook = repository.save(book);

        BookVO createdBookVO = DozerMapper.parseObject(createdBook, BookVO.class);
        createdBookVO.add(linkTo(methodOn(BookController.class).findById(createdBookVO.getId())).withSelfRel());
        return createdBookVO;
    }

    public BookVO update(BookVO bookVO) {
        logger.info("Updating one book");
        if (bookVO == null) throw new RequiredObjectIsNullException();

        if (!repository.existsById(bookVO.getId())) throw new ResourceNotFoundException("No record found with this ID");

        Book updatedBook = repository.save(DozerMapper.parseObject(bookVO, Book.class));

        BookVO updatedBookVO = DozerMapper.parseObject(updatedBook, BookVO.class);
        updatedBookVO.add(linkTo(methodOn(BookController.class).findById(updatedBookVO.getId())).withSelfRel());
        return updatedBookVO;
    }

    public void delete(Long id) {
        logger.info("Deleting one book");
        Book book = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No record found with this ID"));
        repository.delete(book);
    }
}
