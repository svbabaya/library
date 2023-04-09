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
import ru.maxima.springbootapp.library.repositories.BooksRepository;
import ru.maxima.springbootapp.library.services.BooksService;
import ru.maxima.springbootapp.library.services.PeopleRestService;
import ru.maxima.springbootapp.library.services.PeopleService;
import ru.maxima.springbootapp.library.util.*;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/api/people")
public class PeopleRestController {
    private final PeopleService peopleService;
    private final BooksService booksService;
    private final PeopleRestService peopleRestService;
    private final PersonDTO personDTO;
    private final ModelMapper modelMapper;

    private final BooksRepository booksRepository;

    @Autowired
    public PeopleRestController(PeopleService peopleService, PersonDTO personDTO,
                                ModelMapper modelMapper, PeopleRestService peopleRestService,
                                BooksService booksService, BooksRepository booksRepository) {
        this.peopleService = peopleService;
        this.personDTO = personDTO;
        this.modelMapper = modelMapper;
        this.peopleRestService = peopleRestService;
        this.booksService = booksService;
        this.booksRepository = booksRepository;
    }

    @GetMapping("/backdoor")
    public List<Person> getAllPeople() {

        return peopleService.findAll();
    }


    /*****
     * GET localhost:8080/api/people/all
     * Response OK = 200
     *   {
     *     "id": 20,
     *     "name": "Gabriel",
     *     "age": 95,
     *     "email": "gab@mail.com",
     *     "phoneNumber": "+000000-000-000"
     *   }
     * Response NO_CONTENT = 204
     *  {
     *    "message": "There are no readers in library",
     *    "timestamp": "2023-04-06T13:25:30.788+00:00",
     *    "timezone": "Europe/Moscow"
     *  }
     * */
    @GetMapping("/all")
    public List<PersonDTO> getAllPeopleCutInfo() {

        List<PersonDTO> list = peopleService.findAll().stream()
                .map(this::convertToPersonDTO).toList();
        if (list.isEmpty()) {
            throw new PersonListIsEmptyException();
        }
        return list;
    }


    /*****
     * GET localhost:8080/api/people/id
     * Response OK = 200
     *   {
     *     "id": 20,
     *     "name": "Gabriel",
     *     "age": 95,
     *     "email": "gab@mail.com",
     *     "phoneNumber": "+000000-000-000"
     *   }
     * Response BAD_REQUEST = 404
     *   {
     *     "message": "Person not found",
     *     "timestamp": "2023-04-06T08:52:06.208+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     * */
    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") Long id) {

        return convertToPersonDTO(peopleService.findOne(id));
    }

    private PersonDTO convertToPersonDTO(Person person) {

        return modelMapper.map(person, PersonDTO.class);
    }


    /*****
     * POST localhost:8080/api/people/new
     * RequestBody requires JSON
     *   {
     *     "name": "Unique name", // One required field only
     *     "age": 56,
     *     "email": "user@mail.com",
     *     "phoneNumber": "+000000-000-000"
     *   }
     * Response OK = 200
     * Response BAD_REQUEST = 404
     *   {
     *     "message": "name : Name shouldn't be empty",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     * */
    @PostMapping("/new")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder bld = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                bld.append(error.getField())
                        .append(" : ")
                        .append(error.getDefaultMessage());
            });
            throw new PersonNotCreatedException(bld.toString());
        }
        peopleRestService.save(convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }
    private Person convertToPerson(PersonDTO personDTO) {
        Person person = new Person();
        person.setName(personDTO.getName());
        person.setAge(personDTO.getAge());
        person.setEmail(personDTO.getEmail());
        person.setPhoneNumber(personDTO.getPhoneNumber());

        return person;
//        return modelMapper.map(personDTO, Person.class);
    }


    /*****
     * DELETE localhost:8080/api/people/id
     * Response OK = 200 // removed = true
     * Response BAD_REQUEST = 404
     *   {
     *     "message": "Person not found",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        peopleService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    /*****
     * POST localhost:8080/api/people/id/revive
     * Response OK = 200 // removed = false
     * Response BAD_REQUEST = 404
     *   {
     *     "message": "Person not found",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     * */
    @PostMapping("/{id}/revive")
    public ResponseEntity<HttpStatus> revive(@PathVariable("id") Long id) {
        peopleService.revive(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    /*****
     * PUT localhost:8080/api/people/id
     * RequestBody requires JSON
     *   {
     *     "name": "Unique name", // One required field only
     *     "age": 56,
     *     "email": "user@mail.com",
     *     "phoneNumber": "+000000-000-000"
     *   }
     * Response OK = 200
     * Response BAD_REQUEST = 404
     *   {
     *     "message": "name : Name shouldn't be empty",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     *   {
     *     "message": "Person not found",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *   }
     * */
    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") Long id,
                                             @RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder bld = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error -> {
                bld.append(error.getField())
                        .append(" : ")
                        .append(error.getDefaultMessage());
            });
            throw new PersonNotCreatedException(bld.toString());
        }
        peopleService.update(id, convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    /*****
     * POST localhost:8080/api/people/id/books
     * Response OK = 200
     *  {
     *     "id": 12,
     *     "name": "The Silence of the Lambs",
     *     "yearOfProduction": 1988,
     *     "author": "Thomas Harris",
     *     "annotation": "Some text.",
     *     "personId": 31
     *  }
     * Response BAD_REQUEST
     *  {
     *     "message": "Person not found",
     *     "timestamp": "2023-04-06T09:23:34.287+00:00",
     *     "timezone": "Europe/Moscow"
     *  }
     * */
    @GetMapping("/{id}/books")
    public List<BookDTO> getBooksInUse(@PathVariable("id") Long id) {

        List<Book> lst = booksRepository.findByPersonId(id);
        if (lst.isEmpty()) {
            throw new BookListIsEmptyException();
        }
        return lst.stream()
                .map(this::convertToBookDTO).toList();
    }
    private BookDTO convertToBookDTO(Book book) {

        return modelMapper.map(book, BookDTO.class);
    }



    @ExceptionHandler
    public ResponseEntity<PersonErrorResponse> handelException(PersonNotFoundException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                "Person not found", new Date(), TimeZone.getDefault());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<PersonErrorResponse> handelException(PersonNotCreatedException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(), new Date(), TimeZone.getDefault());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<PersonErrorResponse> handelException(PersonListIsEmptyException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                "There are no readers in library", new Date(), TimeZone.getDefault());
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler
    public ResponseEntity<BookErrorResponse> handelException(BookListIsEmptyException e) {
        BookErrorResponse response = new BookErrorResponse(
                "There are no books", new Date(), TimeZone.getDefault());
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }
}
