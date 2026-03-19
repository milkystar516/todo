package com.example.todo.repository;

import com.example.todo.domain.Todo;
import com.example.todo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public class TodoRepository extends JpaRepository<Todo, Long>{
    List<Todo> findByUser(User user);
}
