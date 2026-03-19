package com.example.todo.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor
public class Todo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Todo(String title, User user) {
        this.title = title;
        this.user = user;
        this.completed = false;
    }

    public void complete() {
        this.completed = true;
    }
}
