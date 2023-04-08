package ru.maxima.springbootapp.library.util;

public class PersonNotCreatedException extends RuntimeException {
    public PersonNotCreatedException(String message) {

        super(message);
    }
}
