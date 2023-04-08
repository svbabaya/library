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
import ru.maxima.springbootapp.library.util.PersonNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;
    private final BooksRepository booksRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository, BooksRepository booksRepository) {
        this.peopleRepository = peopleRepository;
        this.booksRepository = booksRepository;
    }

    public List<Person> findAll() {

        return peopleRepository.findAll();
    }

    public List<Book> findBooksInUse(Long personId) {

        return booksRepository.findByPersonId(personId);
    }

    public Person findOne(Long id) {
        Optional<Person> foundPerson =  peopleRepository.findById(id);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    @Transactional
    public void save(Person person) {
        enrichForCreate(person);
        peopleRepository.save(person);
    }

    @Transactional
    public void delete(Long id) {

        enrichForDelete(findOne(id));
//        peopleRepository.deleteById(id);
    }

    @Transactional
    public void revive(Long id) {
        Person personForRevive = findOne(id);
        personForRevive.setRemoved(false);
    }

    @Transactional
    public void update(Long id, Person newDataPerson) {
        Person personForUpdate = findOne(id);
        enrichForUpdate(personForUpdate);
        personForUpdate.setName(newDataPerson.getName());
        personForUpdate.setAge(newDataPerson.getAge());
        personForUpdate.setEmail(newDataPerson.getEmail());
        personForUpdate.setPhoneNumber(newDataPerson.getPhoneNumber());
        peopleRepository.save(personForUpdate);
    }

    public void enrichForCreate(Person person) {
        person.setCreatedAt(LocalDateTime.now());
        person.setRemoved(false);
        person.setCreatedPerson(getUserName());
        person.setRole("ROLE_USER");
        person.setPassword("user");
    }

    public void enrichForUpdate(Person person) {
        person.setUpdatedAt(LocalDateTime.now());
        person.setUpdatedPerson(getUserName());
    }

    public void enrichForDelete(Person person) {
        person.setRemovedAt(LocalDateTime.now());
        person.setRemovedPerson(getUserName());
        person.setRemoved(true);
    }

    public String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
