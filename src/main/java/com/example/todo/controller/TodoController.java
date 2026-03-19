package com.example.todo.controller;

import com.example.todo.domain.Todo;
import com.example.todo.domain.User;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TodoController {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String index(
        @AuthenticationPrincipal UserDetails userDetails,
        Model model
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new IllegalStateException("로그인 정보가 올바르지 않습니다"));
        
        model.addAttribute("todos", todoRepository.findByUser(user));
        return "index";
    }

    @PostMapping("/add")
    public String addTodo(
        @RequestParam String title,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new IllegalStateException("로그인 정보가 올바르지 않습니다"));

        todoRepository.save(
            Todo.builder()
                .title(title)
                .user(user)
                .build()
        );

        return "redirect:/";
    }

    @DeleteMapping("/todo/{todoId}")
    public ResponseEntity<Void> deleteTodo(
        @PathVariable Long todoId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new IllegalStateException("로그인 정보가 올바르지 않습니다"));

        Todo todo = todoRepository.findById(todoId)
        .orElseThrow(() -> new IllegalStateException("존재하지 않는 todo입니다"));

        if (!todo.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        todoRepository.delete(todo);
        return ResponseEntity.noContent().build();
    }
}
