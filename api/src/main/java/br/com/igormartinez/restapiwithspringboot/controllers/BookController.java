package br.com.igormartinez.restapiwithspringboot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.restapiwithspringboot.services.BookService;
import br.com.igormartinez.restapiwithspringboot.utils.MediaType;
import br.com.igormartinez.restapiwithspringboot.data.vo.v1.BookVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/book/v1")
@Tag(name = "Book", description = "Endpoint for managing book")
public class BookController {

    @Autowired
    BookService service;

    @Operation(summary = "Finds all books", description = "Finds all books", 
        tags = {"Book"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", 
                content = {
                    @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = BookVO.class))
                    )
                }),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
    @GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YAML})
    public ResponseEntity<PagedModel<EntityModel<BookVO>>> findAll(
        @RequestParam(value = "page", defaultValue = "0") Integer page,
        @RequestParam(value = "size", defaultValue = "12") Integer size,
        @RequestParam(value = "direction", defaultValue = "asc") String direction) {
            Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "title"));
            return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Finds a book by id", description = "Finds a book by id", 
        tags = {"Book"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", 
                content = @Content(schema = @Schema(implementation = BookVO.class))),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
    @GetMapping(value = "/{id}", 
        produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YAML})
    public BookVO findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Add a new book", description = "Add a new book", 
        tags = {"Book"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", 
                content = @Content(schema = @Schema(implementation = BookVO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
    @PostMapping(
        consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YAML}, 
        produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YAML})
    public BookVO create(@RequestBody BookVO bookVO) {
        return service.create(bookVO);
    }

    @Operation(summary = "Update a book", description = "Update a book", 
        tags = {"Book"},
        responses = {
            @ApiResponse(description = "Success", responseCode = "200", 
                content = @Content(schema = @Schema(implementation = BookVO.class))),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
    @PutMapping(
        consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YAML}, 
        produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YAML})
    public BookVO update(@RequestBody BookVO bookVO) {
        return service.update(bookVO);
    }

    @Operation(summary = "Delete a book", description = "Delete a book", 
        tags = {"Book"},
        responses = {
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(value="id")Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
