package com.kas.online_book_shop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.model.Publisher;
import com.kas.online_book_shop.repository.PublisherRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PublisherRepositoryTest {
    @Autowired
    private PublisherRepository publisherRepository;

    @Transactional
    @Test
    void addValidPublisher() {
        long currentNumberPublisher = publisherRepository.count();
        var publisher = Publisher.builder().name("publisher").website("http://website.com").build();
        publisherRepository.save(publisher);
        assertThat(publisherRepository.count()).isEqualTo(currentNumberPublisher + 1);
        var newPublisher = publisherRepository.findAll().get((int) currentNumberPublisher);
        assertThat(newPublisher.getName()).isEqualTo("publisher");
        assertThat(newPublisher.getWebsite()).isEqualTo("http://website.com");
    }

    @Test
    @Transactional
    void addInvalidAuthorWithInvalidWebsite() {
        long currentNumberPublisher = publisherRepository.count();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            Publisher publisher = Publisher.builder().name("publisher").website("company").build();
            publisherRepository.save(publisher);
            assertThat(publisherRepository.count()).isEqualTo(currentNumberPublisher);
        });
        assertThat(exception.getMessage()).contains("Invalid url");
    }

    @Test
    @Transactional
    void addInvalidAuthorWithEmptyName() {
        long currentNumberPublisher = publisherRepository.count();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            Publisher publisher = Publisher.builder().name("").website("http://website.com").build();
            publisherRepository.save(publisher);
            assertThat(publisherRepository.count()).isEqualTo(currentNumberPublisher);
        });
        assertThat(exception.getMessage()).contains("The publisher name is required");
    }

    @Test
    @Transactional
    void updatePublisher() {
        // Add a publisher first
        Publisher publisher = Publisher.builder().name("ToUpdate").website("http://website.com").build();
        publisherRepository.save(publisher);

        // Update the publisher's information
        Publisher updatedPublisher = Publisher.builder().name("UpdatedName").website("http://newwebsite.com").build();
        updatedPublisher.setId(publisher.getId());
        publisherRepository.save(updatedPublisher);

        // Retrieve the updated publisher from the repository
        Publisher retrievedPublisher = publisherRepository.findById(publisher.getId()).orElse(null);

        // Verify that the publisher's information has been updated
        assertThat(retrievedPublisher).isNotNull();
        assertThat(retrievedPublisher.getName()).isEqualTo("UpdatedName");
        assertThat(retrievedPublisher.getWebsite()).isEqualTo("http://newwebsite.com");
    }

}
