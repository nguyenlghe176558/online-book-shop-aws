package com.kas.online_book_shop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.enums.BookState;
import com.kas.online_book_shop.model.Book;
import com.kas.online_book_shop.model.BookCategory;
import com.kas.online_book_shop.model.BookCollection;
import com.kas.online_book_shop.repository.BookCategoryRepository;
import com.kas.online_book_shop.repository.BookCollectionRepository;
import com.kas.online_book_shop.repository.BookRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private BookCollectionRepository bookCollectionRepository;

    @Test
    public void testFindByTitleContainingAndCategoryAndCollectionsAndState_WithAllNullParameters() {
        Page<Book> result = bookRepository
        .findByTitleContainingAndCategoryAndCollectionsAndState("", null, null, null,
                PageRequest.of(0, 1000));
        Page<Book> allBooks = bookRepository.findAll(PageRequest.of(0, 1000));
        assertEquals(allBooks.getTotalElements(), result.getTotalElements());
    }

    @Test
    public void testFindByTitleContainingAndCategoryAndCollectionsAndState_WithContent() {
        Page<Book> result = bookRepository
        .findByTitleContainingAndCategoryAndCollectionsAndState("Doraemon", null,
                null, null, PageRequest.of(0, 1000));
        assertEquals(24, result.getTotalElements());
        result.getContent().forEach((book) -> {
            assertThat(book.getTitle()).containsIgnoringCase("Doraemon");
        });
    }

    @Test
    public void testFindByTitleContainingAndCategoryAndCollectionsAndState_WithActiveState() {
        Page<Book> result = bookRepository
        .findByTitleContainingAndCategoryAndCollectionsAndState("", BookState.ACTIVE,
                null, null, PageRequest.of(0, 1000));
        assertEquals(63, result.getTotalElements());
        result.getContent().forEach((book) -> {
            assertEquals(book.getState(), BookState.ACTIVE);
        });
    }

    @Test
    public void testFindByTitleContainingAndCategoryAndCollectionsAndState_WithNameAndState() {
        Page<Book> result = bookRepository
        .findByTitleContainingAndCategoryAndCollectionsAndState("Doraemon",
                BookState.ACTIVE, null, null, PageRequest.of(0, 1000));
        assertEquals(24, result.getTotalElements());
        result.getContent().forEach((book) -> {
            assertEquals(book.getState(), BookState.ACTIVE);
            assertThat(book.getTitle()).containsIgnoringCase("Doraemon");
        });
    }

    @Test
    public void testFindByTitleContainingAndCategoryAndCollectionsAndState_WithNotExistNameAndState() {
        Page<Book> result = bookRepository
        .findByTitleContainingAndCategoryAndCollectionsAndState("Doraemon1",
                BookState.ACTIVE, null, null, PageRequest.of(0, 1000));
        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void testFindByTitleContainingAndCategoryAndCollectionsAndState_WithCategory() {
        BookCategory bookCategory = bookCategoryRepository.findById(1L).get();
        Page<Book> result = bookRepository.findByTitleContainingAndCategoryAndCollectionsAndState("", null,
                bookCategory, null, PageRequest.of(0, 1000));
        assertEquals(62, result.getTotalElements());
        result.getContent().forEach((book) -> {
            assertEquals(book.getCategory(), bookCategory);
        });
    }

    @Test
    public void testFindByTitleContainingAndCategoryAndCollectionsAndState_WithCategoryAndState() {
        BookCategory bookCategory = bookCategoryRepository.findById(1L).get();
        Page<Book> result = bookRepository.findByTitleContainingAndCategoryAndCollectionsAndState("", BookState.ACTIVE,
                bookCategory, null, PageRequest.of(0, 1000));
        result.getContent().forEach((book) -> {
            assertEquals(book.getState(), BookState.ACTIVE);
            assertEquals(book.getCategory(), bookCategory);
        });
    }

    @Test
    @Transactional
    public void testFindByTitleContainingAndCategoryAndCollectionsAndState_WithCollection() {
        BookCollection collection = bookCollectionRepository.findById(1L).get();
        Page<Book> result = bookRepository.findByTitleContainingAndCategoryAndCollectionsAndState("", null, null,
                collection, PageRequest.of(0, 1000));
        assertEquals(9, result.getTotalElements());
        result.getContent().forEach((book) -> {
            assertThat(book.getCollections().contains(collection));
        });
    }

    @Test
    @Transactional
    public void testFindByTitleContainingAndCategoryAndCollectionsAndState_WithAll() {
        BookCollection collection = bookCollectionRepository.findById(27L).get();
        BookCategory category = bookCategoryRepository.findById(1L).get();
        Page<Book> result = bookRepository.findByTitleContainingAndCategoryAndCollectionsAndState("doraemon",
                BookState.ACTIVE, category,
                collection, PageRequest.of(0, 1000));
        assertEquals(23, result.getTotalElements());
        result.getContent().forEach((book) -> {
            assertThat(book.getCollections().contains(collection));
            assertThat(book.getCategory().equals(category));
            assertThat(book.getTitle()).containsIgnoringCase("doraemon");
        });
    }
}
