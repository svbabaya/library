package ru.maxima.springbootapp.library.util;

public class BookNotCreatedException extends RuntimeException {
    public BookNotCreatedException(String message) {

        super(message);
    }
}
