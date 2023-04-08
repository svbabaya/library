package ru.maxima.springbootapp.library.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.maxima.springbootapp.library.models.Book;
import ru.maxima.springbootapp.library.services.BooksService;

import java.security.Principal;

@Controller
@RequestMapping("/reader")
public class ReaderController {

    private final BooksService booksService;

    @Autowired
    public ReaderController(BooksService booksService) {

        this.booksService = booksService;
    }

    @GetMapping()
    public String index(@ModelAttribute("book") Book book,
                        Model model, Principal principal) {

        model.addAttribute("booksInUse", booksService.findBooksByPrincipal(principal.getName()));
        model.addAttribute("booksForChoose", booksService.findUniqueFree());
        return "reader/index";
    }

    @GetMapping("unlink/{id}")
    public String openUnlinkPage(@PathVariable("id") Long id,
                                Model model) {
        model.addAttribute("book", booksService.findOne(id));
        return "reader/unlink";
    }

    @PostMapping("unlink/{id}")
    public String unlink(@PathVariable("id") Long id) {
        booksService.unlink(id);
        return "redirect:/reader";
    }

    @GetMapping("/assign")
    public String openAssignPage(@ModelAttribute("book") Book book, Model model) {
        model.addAttribute("book", booksService.findOne(book.getId()));
        return "reader/assign";
    }

    @PostMapping("/assign/{id}")
    public String assignBookToPrincipal(@PathVariable("id") Long id) {
        booksService.assignBookToPrincipal(id);
        return "redirect:/reader";
    }

}
