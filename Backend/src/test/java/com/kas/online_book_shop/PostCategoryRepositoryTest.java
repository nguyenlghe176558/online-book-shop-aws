package com.kas.online_book_shop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.model.PostCategory;
import com.kas.online_book_shop.repository.PostCategoryRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@ActiveProfiles("test")
public class PostCategoryRepositoryTest {
    @Autowired
    private PostCategoryRepository postCategoryRepository;

    @Test
    @Transactional
    void addValidBookCategory() {
        long currentNumberPostCategory = postCategoryRepository.count();
        var postCategory = PostCategory.builder().name("Category").build();
        postCategoryRepository.save(postCategory);
        assertThat(postCategoryRepository.count()).isEqualTo(currentNumberPostCategory + 1);
        var newPostCategory = postCategoryRepository.findAll().get((int) currentNumberPostCategory);
        assertThat(newPostCategory.getName()).isEqualTo("Category");
    }

    @Test
    @Transactional
    void addInvalidPostCategoryWithEmptyName() {
        long currentNumberPostCategory = postCategoryRepository.count();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            var postCategory = PostCategory.builder().name("").build();
            postCategoryRepository.save(postCategory);
            assertThat(postCategoryRepository.count()).isEqualTo(currentNumberPostCategory);
        });
        assertThat(exception.getMessage()).contains("The category name is required");
    }

    @Test
    @Transactional
    void updatePostCategoryName() {
        var postCategory = PostCategory.builder().name("OriginalCategory").build();
        postCategoryRepository.save(postCategory);

        // Update the category name
        postCategory.setName("UpdatedCategory");
        postCategoryRepository.save(postCategory);

        var updatedCategory = postCategoryRepository.findById(postCategory.getId()).orElse(null);
        assertNotNull(updatedCategory);
        assertEquals("UpdatedCategory", updatedCategory.getName());
    }
}
