package ru.maxima.springbootapp.library.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.maxima.springbootapp.library.models.Book;
import ru.maxima.springbootapp.library.repositories.BooksRepository;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class BooksRestService {

    private final BooksRepository booksRepository;
    @Autowired
    public BooksRestService(BooksRepository booksRepository) {

        this.booksRepository = booksRepository;
    }

    @Transactional
    public void save(Book book) {
        enrichForCreate(book);
        booksRepository.save(book);
    }

    public void enrichForCreate(Book book) {
        book.setCreatedAt(LocalDateTime.now());
        book.setCreatedPerson("Postman");
        book.setRemoved(false);
    }

}
