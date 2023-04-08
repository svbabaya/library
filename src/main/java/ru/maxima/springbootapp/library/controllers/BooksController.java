package ru.maxima.springbootapp.library.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.maxima.springbootapp.library.models.Book;
import ru.maxima.springbootapp.library.models.Person;
import ru.maxima.springbootapp.library.services.BooksService;
import ru.maxima.springbootapp.library.services.PeopleService;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;
    private final PeopleService peopleService;
    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("books", booksService.findAll());
        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, @ModelAttribute Person person,
                       Model model) {
        model.addAttribute("book", booksService.findOne(id));
        model.addAttribute("owner", booksService.findOwner(id));
        model.addAttribute("people", peopleService.findAll());
        return "books/show";
    }

    @GetMapping("/new")
    public String newBookPageOpen(@ModelAttribute("book") Book book,
                              Model model) {
        model.addAttribute("booksUnique", booksService.findUnique());
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "books/new";
        booksService.save(book);
        return "redirect:/books";
    }

    @PostMapping("/duplicate")
    public String duplicate(@ModelAttribute("book") Book book) {
        booksService.duplicate(book.getId());
        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        booksService.delete(id);
        return "redirect:/books";
    }

    @PostMapping("/{id}/revive")
    public String revive(@PathVariable("id") Long id) {
        booksService.revive(id);
        return "redirect:/books";
    }

    @GetMapping("/{id}/update")
    public String updateBookPageOpen(Model model, @PathVariable("id") Long id) {
        model.addAttribute("book", booksService.findOne(id));
        return "books/update";
    }

    @PutMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult,
                         @PathVariable("id") Long id) {
        if (bindingResult.hasErrors())
            return "books/update";
        booksService.update(id, book);
        return "redirect:/books/{id}";
    }

    @PatchMapping("/{id}")
    public String unlink(@PathVariable("id") Long id) {
        booksService.unlink(id);
        return "redirect:/books/{id}";
    }

    @PostMapping("/{id}")
    public String assignBookToPerson(@PathVariable("id") Long bookId,
                         @ModelAttribute("person") Person person) {
        booksService.assignBookToPerson(bookId, person);
        return "redirect:/books/{id}";
    }

}
