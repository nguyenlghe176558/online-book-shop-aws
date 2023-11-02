package com.kas.online_book_shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kas.online_book_shop.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByName(String name);
}
