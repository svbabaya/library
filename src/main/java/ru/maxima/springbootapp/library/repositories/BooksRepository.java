package ru.maxima.springbootapp.library.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maxima.springbootapp.library.models.Book;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Long> {
    List<Book> findByPersonId(Long id);

    List<Book> findAllByRemovedAndPersonId(Boolean removed, Long personId);


}
