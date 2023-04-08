package ru.maxima.springbootapp.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maxima.springbootapp.library.models.Person;
import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByName(String username);

}
