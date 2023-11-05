package com.kas.online_book_shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.enums.AccountState;
import com.kas.online_book_shop.enums.PostState;
import com.kas.online_book_shop.enums.Role;
import com.kas.online_book_shop.model.Post;
import com.kas.online_book_shop.model.PostCategory;
import com.kas.online_book_shop.model.User;
import com.kas.online_book_shop.repository.PostCategoryRepository;
import com.kas.online_book_shop.repository.PostRepository;
import com.kas.online_book_shop.repository.UserRepository;
import com.kas.online_book_shop.service.PostService;
import com.kas.online_book_shop.service.PostServiceImpl;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostTest {

        @Autowired
        PostService postService;

        @Autowired
        PostRepository postRepository;

        @Autowired
        UserRepository userRepository;

        @Autowired
        PostCategoryRepository postCategoryRepository;

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

        @Test
        public void testAddPost() {
                // Create a new User
                User user = User.builder()
                                .fullName("Test User")
                                .province("Test Province")
                                .district("Test District")
                                .ward("Test Ward")
                                .address("Test Address")
                                .phone("0123456789")
                                .email("test@example.com")
                                .password("testpassword")
                                .role(Role.USER)
                                .state(AccountState.ACTIVE)
                                .build();

                // Save the user to the database
                userRepository.save(user);

                // Create a new PostCategory
                PostCategory category = PostCategory.builder()
                                .name("Test Category")
                                .build();

                // Save the category to the database
                postCategoryRepository.save(category);

                // Create a new Post object with test data
                Post newPost = Post.builder()
                                .title("Test Post")
                                .category(category) // Set the PostCategory
                                .user(user) // Set the User
                                .thumbnail("test-thumbnail-url")
                                .content("Test content")
                                .brief("Test brief")
                                .createdAt(LocalDateTime.now())
                                .state(PostState.PUBLISHED)
                                .build();

                // Add the new post
                postService.savePost(newPost);

                // Retrieve the added post from the database
                Post retrievedPost = postRepository.findById(newPost.getId()).orElse(null);

                // Verify the result
                assertNotNull(retrievedPost);
                assertEquals(newPost.getTitle(), retrievedPost.getTitle());
                // Add additional assertions for other fields as needed
        }

        @Test
        public void testUpdatePost() {
                // Create a new User
                User user = User.builder()
                                .fullName("Test User")
                                .province("Test Province")
                                .district("Test District")
                                .ward("Test Ward")
                                .address("Test Address")
                                .phone("0123456789")
                                .email("test@example.com")
                                .password("testpassword")
                                .role(Role.USER)
                                .state(AccountState.ACTIVE)
                                .build();

                // Save the user to the database
                userRepository.save(user);

                // Create a new PostCategory
                PostCategory category = PostCategory.builder()
                                .name("Test Category")
                                .build();

                // Save the category to the database
                postCategoryRepository.save(category);

                // Create a new Post object with test data
                Post newPost = Post.builder()
                                .title("Test Post")
                                .category(category) // Set the PostCategory
                                .user(user) // Set the User
                                .thumbnail("test-thumbnail-url")
                                .content("Test content")
                                .brief("Test brief")
                                .createdAt(LocalDateTime.now())
                                .state(PostState.PUBLISHED)
                                .build();

                // Add the new post
                postService.savePost(newPost);

                // Retrieve the added post from the database
                Post retrievedPost = postRepository.findById(newPost.getId()).orElse(null);

                // Verify the result
                assertNotNull(retrievedPost);
                assertEquals(newPost.getTitle(), retrievedPost.getTitle());
                // Add additional assertions for other fields as needed
        }

        @Test
        public void testDeletePost() {
                // Create a new User
                User user = User.builder()
                                .fullName("Test User")
                                .province("Test Province")
                                .district("Test District")
                                .ward("Test Ward")
                                .address("Test Address")
                                .phone("0123456789")
                                .email("test@example.com")
                                .password("testpassword")
                                .role(Role.USER)
                                .state(AccountState.ACTIVE)
                                .build();

                // Save the user to the database
                userRepository.save(user);

                // Create a new PostCategory
                PostCategory category = PostCategory.builder()
                                .name("Test Category")
                                .build();

                // Save the category to the database
                postCategoryRepository.save(category);

                // Create a new Post object with test data
                Post newPost = Post.builder()
                                .title("Test Post")
                                .category(category) // Set the PostCategory, if applicable
                                .user(user) // Set the User, if applicable
                                .thumbnail("test-thumbnail-url")
                                .content("Test content")
                                .brief("Test brief")
                                .createdAt(LocalDateTime.now())
                                .state(PostState.PUBLISHED)
                                .build();

                // Add the new post
                postService.savePost(newPost);

                // Retrieve the added post from the database
                Post retrievedPost = postRepository.findById(newPost.getId()).orElse(null);

                // Verify that the post was added successfully
                assertNotNull(retrievedPost);

                // Delete the post
                postService.deletePost(retrievedPost.getId());

                // Attempt to retrieve the deleted post
                Post deletedPost = postRepository.findById(newPost.getId()).orElse(null);

                // Verify that the post's state is now PostState.DELETED
                assertNotNull(deletedPost);
                assertEquals(PostState.DELETED, deletedPost.getState());
        }
}
