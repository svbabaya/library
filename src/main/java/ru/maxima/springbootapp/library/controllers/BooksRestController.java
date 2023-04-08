package ru.maxima.springbootapp.library.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.maxima.springbootapp.library.dto.BookDTO;
import ru.maxima.springbootapp.library.dto.PersonDTO;
import ru.maxima.springbootapp.library.models.Book;
import ru.maxima.springbootapp.library.models.Person;
import ru.maxima.springbootapp.library.services.BooksRestService;
import ru.maxima.springbootapp.library.services.BooksService;
import ru.maxima.springbootapp.library.services.PeopleService;
import ru.maxima.springbootapp.library.util.*;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/books")
public class BooksRestController {

    private final PeopleService peopleService;
    private final BooksService booksService;
    private final BooksRestService booksRestService;
    private final BookDTO bookDTO;

    private final PersonDTO personDTO;
    private final ModelMapper modelMapper;
    @Autowired
    public BooksRestController(PeopleService peopleService, BooksService booksService,
                               BooksRestService booksRestService, BookDTO bookDTO,
                               PersonDTO personDTO, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.booksService = booksService;
        this.booksRestService = booksRestService;
        this.bookDTO = bookDTO;
        this.personDTO = personDTO;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/backdoor")
    public List<Book> getAllBooks() {

        return booksService.findAll();
    }


    /*****
     * GET localhost:8080/api/books/all
     * Response OK = 200
     *  {
     *    "id": 13,
     *    "name": "The Silence of the Lambs",
     *    "yearOfProduction": 1988,
     *    "author": "Thomas Harris",
     *    "annotation": "Some text.",
     *    "personId": null
     *  }
     * Response NO_CONTENT = 204
     *  {
     *    "message": "There are no books in library",
     *    "timestamp": "2023-04-06T13:25:30.788+00:00",
     *    "timezone": "Europe/Moscow"
     *  }
     * */
    @GetMapping("/all")
    public List<BookDTO> getAllBooksCutInfo() {

        List<BookDTO> list = booksService.findAll().stream()
                .map(this::convertToBookDTO).toList();
        if (list.isEmpty()) {
            throw new BookListIsEmptyException();
        }
        return list;
    }


    /*****
     * GET localhost:8080/api/books/id
     * Response OK = 200
     *  {
     *    "id": 13,
     *    "name": "The Silence of the Lambs",
     *    "yearOfProduction": 1988,
     *    "author": "Thomas Harris",
     *    "annotation": "Some text.",
     *    "personId": null
     *  }
     * Response BAD_REQUEST = 404
     *  {
     *    "message": "Book not found",
     *    "timestamp": "2023-04-06T13:25:30.788+00:00",
     *    "timezone": "Europe/Moscow"
     *  }
     * */
    @GetMapping("/{id}")
    public BookDTO getBook(@PathVariable("id") Long id) {

        return convertToBookDTO(booksService.findOne(id));
    }

    private BookDTO convertToBookDTO(Book book) {

        return modelMapper.map(book, BookDTO.class);
    }


    /*****
     * POST localhost:8080/api/books/new
     * RequestBody requires JSON
     *  {
     *    "name": "The Silence of the Lambs", // Require field
     *    "yearOfProduction": 1988,
     *    "author": "Thomas Harris",          // Require field
     *    "annotation": "Well."
     *  }
     * Response OK = 200
     * Response BAD_REQUEST = 404
     *  {
     *    "message": "name/author : Title/Author shouldn't be empty",
     *    "timestamp": "2023-04-06T13:25:30.788+00:00",
     *    "timezone": "Europe/Moscow"
     *  }
     * */
    @PostMapping("/new")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid BookDTO bookDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder bld = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                bld.append(error.getField())
                        .append(" : ")
                        .append(error.getDefaultMessage());
            });
            throw new BookNotCreatedException(bld.toString());
        }
        booksRestService.save(convertToBook(bookDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }
    private Book convertToBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setName(bookDTO.getName());
        book.setYearOfProduction(bookDTO.getYearOfProduction());
        book.setAuthor(bookDTO.getAuthor());
        book.setAnnotation(bookDTO.getAnnotation());
        book.setPersonId(bookDTO.getPersonId());

        return book;
//        return modelMapper.map(bookDTO, Book.class);
    }


    /*****
     * DELETE localhost:8080/api/books/id
     * Response OK = 200 // removed = true
     * Response BAD_REQUEST = 404
     *   {
     *     "message": "Book not found",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        booksService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    /*****
     * POST localhost:8080/api/books/id/revive
     * Response OK = 200 // removed = false
     * Response BAD_REQUEST = 404
     *   {
     *     "message": "Book not found",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     * */
    @PostMapping("/{id}/revive")
    public ResponseEntity<HttpStatus> revive(@PathVariable("id") Long id) {
        booksService.revive(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    /*****
     * PUT localhost:8080/api/books/id
     * RequestBody requires JSON
     *   {
     *    "name": "The Silence of the Lambs", // Required field
     *    "yearOfProduction": 1988,
     *    "author": "Thomas Harris",          // Required field
     *    "annotation": "Well."
     *    "personId": 10                      // Assign to person (id=10)
     *   }
     * Response OK = 200
     * Response BAD_REQUEST = 404
     *   {
     *     "message": "name/author : Title/Author shouldn't be empty",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     *   {
     *     "message": "Book not found",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     * */
    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") Long id,
                                             @RequestBody @Valid BookDTO bookDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder bld = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                bld.append(error.getField())
                        .append(" : ")
                        .append(error.getDefaultMessage());
            });
            throw new BookNotCreatedException(bld.toString());
        }
        booksService.update(id, convertToBook(bookDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    /*****
     * POST localhost:8080/api/books/id/owner
     * Response OK = 200
     * Response BAD_REQUEST = 404
     *   {
     *     "message": "Book not found",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     *   {
     *     "message": "Book is free",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     * */
    @GetMapping("/{id}/owner")
    public PersonDTO getOwnerOfBook(@PathVariable("id") Long id) {
        Long personId = booksService.findOne(id).getPersonId();
        if (personId == null) {
            throw new BookIsFreeException();
        }
        Person person = peopleService.findOne(personId);
        return convertToPersonDTO(person);
    }
    private PersonDTO convertToPersonDTO(Person person) {

        return modelMapper.map(person, PersonDTO.class);
    }


    @ExceptionHandler
    public ResponseEntity<BookErrorResponse> handelException(BookNotFoundException e) {
        BookErrorResponse response = new BookErrorResponse(
                "Book not found", new Date(), TimeZone.getDefault());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<BookErrorResponse> handelException(BookNotCreatedException e) {
        BookErrorResponse response = new BookErrorResponse(
                e.getMessage(), new Date(), TimeZone.getDefault());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<BookErrorResponse> handelException(BookListIsEmptyException e) {
        BookErrorResponse response = new BookErrorResponse(
                "There are no books in this library", new Date(), TimeZone.getDefault());
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    public ResponseEntity<BookErrorResponse> handelException(BookIsFreeException e) {
        BookErrorResponse response = new BookErrorResponse(
                "Free book", new Date(), TimeZone.getDefault());
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

}
