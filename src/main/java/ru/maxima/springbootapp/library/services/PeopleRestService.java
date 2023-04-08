package ru.maxima.springbootapp.library.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maxima.springbootapp.library.models.Person;
import ru.maxima.springbootapp.library.repositories.PeopleRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class PeopleRestService {

    private final PeopleRepository peopleRepository;
    @Autowired
    public PeopleRestService(PeopleRepository peopleRepository) {

        this.peopleRepository = peopleRepository;
    }

    @Transactional
    public void save(Person person) {
        enrichForCreate(person);
        peopleRepository.save(person);
    }

    public void enrichForCreate(Person person) {
        person.setCreatedAt(LocalDateTime.now());
        person.setRemoved(false);
        person.setCreatedPerson("Postman");
        person.setRole("ROLE_USER");
        person.setPassword("user");
    }

}
