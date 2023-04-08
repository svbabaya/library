package ru.maxima.springbootapp.library.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maxima.springbootapp.library.models.Book;
import ru.maxima.springbootapp.library.models.Person;
import ru.maxima.springbootapp.library.repositories.BooksRepository;
import ru.maxima.springbootapp.library.repositories.PeopleRepository;
import ru.maxima.springbootapp.library.util.BookNotFoundException;
import ru.maxima.springbootapp.library.util.PersonNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BooksRepository booksRepository;
    private final PeopleRepository peopleRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository, PeopleRepository peopleRepository) {
        this.booksRepository = booksRepository;
        this.peopleRepository = peopleRepository;
    }

    public List<Book> findAll() {

        return booksRepository.findAll();
    }

    public List<Book> findUnique() {
        HashSet<Book> set = new HashSet<>(booksRepository.findAll());
        return new ArrayList<>(set);
    }

    public List<Book> findUniqueFree() {
        HashSet<Book> set = new HashSet<>(booksRepository.findAllByRemovedAndPersonId(false, null));
        return new ArrayList<>(set);
    }

    public Book findOne(Long id) {
        Optional<Book> foundBook = booksRepository.findById(id);
        return foundBook.orElseThrow(BookNotFoundException::new);
    }


    public List<Book> findBooksByPrincipal(String username) {
        Optional<Person> foundPerson = peopleRepository.findByName(username);
        return foundPerson.map(person -> booksRepository.findByPersonId(person.getId())).orElse(null);
    }

    @Transactional
    public Person findOwner(Long bookId) {
        Long personId = findOne(bookId).getPersonId();
        if (personId != null) {
            Optional<Person> foundOwner = peopleRepository.findById(personId);
            return foundOwner.orElseThrow(PersonNotFoundException::new);
        } else
            return null;
    }

    @Transactional
    public void save(Book book) {
        enrichForCreate(book);
        booksRepository.save(book);
    }
    @Transactional
    public void delete(Long id) {
        enrichForDelete(findOne(id));
//        booksRepository.deleteById(id);
    }

    @Transactional
    public void revive(Long id) {
        Book bookForRevive = findOne(id);
        bookForRevive.setRemoved(false);
    }

    @Transactional
    public void update(Long id, Book newDataBook) {
        Book bookForUpdate = findOne(id);
        enrichForUpdate(bookForUpdate);
        bookForUpdate.setName(newDataBook.getName());
        bookForUpdate.setYearOfProduction(newDataBook.getYearOfProduction());
        bookForUpdate.setAuthor(newDataBook.getAuthor());
        bookForUpdate.setAnnotation(newDataBook.getAnnotation());
        bookForUpdate.setPersonId(newDataBook.getPersonId());
        booksRepository.save(bookForUpdate);
    }
    @Transactional
    public void unlink(Long bookId) {
        Book bookForUnlink = findOne(bookId);
        bookForUnlink.setPersonId(null);
        booksRepository.save(bookForUnlink);
    }

    @Transactional
    public void assignBookToPerson(Long bookId, Person person) {
        Book bookForAssign = findOne(bookId);
        bookForAssign.setPersonId(person.getId());
        booksRepository.save(bookForAssign);
    }

    @Transactional
    public void assignBookToPrincipal(Long bookId) {
        Book bookForAssign = findOne(bookId);
        Optional<Person> foundPerson = peopleRepository.findByName(getUserName());
        foundPerson.ifPresent(person -> bookForAssign.setPersonId(person.getId()));
        booksRepository.save(bookForAssign);
    }

    @Transactional
    public void duplicate(Long bookId) {
        Book bookForDuplicate = findOne(bookId);
        Book newDataBook = new Book();
        newDataBook.setName(bookForDuplicate.getName());
        newDataBook.setAuthor(bookForDuplicate.getAuthor());
        newDataBook.setYearOfProduction(bookForDuplicate.getYearOfProduction());
        newDataBook.setAnnotation(bookForDuplicate.getAnnotation());
        enrichForCreate(newDataBook);
        booksRepository.save(newDataBook);
    }

    public void enrichForCreate(Book book) {
        book.setCreatedAt(LocalDateTime.now());
        book.setCreatedPerson(getUserName());
        book.setRemoved(false);
    }

    public void enrichForUpdate(Book book) {
        book.setUpdatedAt(LocalDateTime.now());
        book.setUpdatedPerson(getUserName());
    }

    public void enrichForDelete(Book book) {
        book.setRemovedAt(LocalDateTime.now());
        book.setRemovedPerson(getUserName());
        book.setRemoved(true);
    }

    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
