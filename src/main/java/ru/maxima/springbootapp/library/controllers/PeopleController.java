package ru.maxima.springbootapp.library.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.maxima.springbootapp.library.models.Person;
import ru.maxima.springbootapp.library.services.PeopleService;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;

    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());
        return "people/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("person", peopleService.findOne(id));
        model.addAttribute("booksInUse", peopleService.findBooksInUse(id));
        return "people/show";
    }

    @GetMapping("/new")
    public String newReaderPageOpen(@ModelAttribute("person") Person person) {

        return "people/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "people/new";
        peopleService.save(person);
        return "redirect:/people";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        peopleService.delete(id);
        return "redirect:/people";
    }

    @PostMapping("/{id}/revive")
    public String revive(@PathVariable("id") Long id) {
        peopleService.revive(id);
        return "redirect:/people";
    }

    @GetMapping("/{id}/update")
    public String updateReaderPageOpen(Model model, @PathVariable("id") Long id) {
        model.addAttribute("person", peopleService.findOne(id));
        return "people/update";
    }

    @PutMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @PathVariable("id") Long id) {
        if (bindingResult.hasErrors())
            return "people/update";
        peopleService.update(id, person);
        return "redirect:/people/{id}";
    }



}
