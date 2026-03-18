package com.example.todo;

import java.util.ArrayList; 
import java.util.List; 
import java.util.concurrent.atomic.AtomicLong; 
import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.*;

@Controller
public class TodoController {
    private final List<Todo> todos = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("todos", todos);
        return "index";
    }

    @PostMapping("/add")
    public String addTodo(@RequestParam String title) {
        Todo todo = new Todo(
            idGenerator.incrementAndGet(),
            title,
            false
        );

        todos.add(todo);
        return "redirect:/";
    }
}
