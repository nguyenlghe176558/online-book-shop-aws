package com.kas.online_book_shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.model.Post;
import com.kas.online_book_shop.repository.PostRepository;
import com.kas.online_book_shop.service.PostServiceImpl;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostTest {
    
    @Autowired
    private PostServiceImpl postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    public void testGetAllPosts() {
        // Initialize the service manually
        postService = new PostServiceImpl(postRepository);

        // Call the method you want to test
        var retrievedPosts = postService.getAll();

        // Verify the result
        assertNotNull(retrievedPosts);
        assertEquals(0, retrievedPosts.size());
    }

    
}
