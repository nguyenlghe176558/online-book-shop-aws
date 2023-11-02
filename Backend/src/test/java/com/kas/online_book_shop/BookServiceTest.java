package com.kas.online_book_shop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.enums.BookState;
import com.kas.online_book_shop.exception.ISBNDuplicateException;
import com.kas.online_book_shop.exception.ResourceNotFoundException;
import com.kas.online_book_shop.model.Author;
import com.kas.online_book_shop.model.Book;
import com.kas.online_book_shop.model.BookCollection;
import com.kas.online_book_shop.model.Image;
import com.kas.online_book_shop.repository.AuthorRepository;
import com.kas.online_book_shop.repository.BookCategoryRepository;
import com.kas.online_book_shop.repository.BookCollectionRepository;
import com.kas.online_book_shop.repository.BookRepository;
import com.kas.online_book_shop.repository.LanguageRepository;
import com.kas.online_book_shop.repository.PublisherRepository;
import com.kas.online_book_shop.service.BookService;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class BookServiceTest {
    @Autowired
    BookService bookService;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookCategoryRepository categoryRepository;

    @Autowired
    BookCollectionRepository collectionRepository;

    @Autowired
    LanguageRepository languageRepository;

    @Autowired
    PublisherRepository publisherRepository;

    @Test
    @Transactional
    void saveBook_valid() {
        List<Author> authors = new ArrayList<Author>();
        var author1 = authorRepository.findById(1L).get();
        var author2 = authorRepository.findById(2L).get();
        var category = categoryRepository.findById(1L).get();
        var collection = collectionRepository.findById(1L).get();
        var language = languageRepository.findById(1L).get();
        var publisher = publisherRepository.findById(1L).get();
        var image = Image.builder().link("https://image.com").description("description").build();
        List<Image> images = new ArrayList<Image>();
        images.add(image);
        List<BookCollection> collections = new ArrayList<BookCollection>();
        collections.add(collection);
        authors.add(author1);
        authors.add(author2);
        var book = Book.builder()
                .title("title")
                .ISBN("978-604-2-26673-8")
                .authors(authors)
                .cover("cover")
                .page(100)
                .description("description")
                .category(category)
                .collections(collections)
                .price(1000L)
                .discount(0.1f)
                .language(language)
                .images(images)
                .weight(100)
                .size("size")
                .stock(100)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        var savedBook = bookService.saveBook(book);
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("title");
        assertThat(savedBook.getISBN()).isEqualTo("978-604-2-26673-8");
        assertThat(savedBook.getCover()).isEqualTo("cover");
        assertThat(savedBook.getPage()).isEqualTo(100);
        assertThat(savedBook.getDescription()).isEqualTo("description");
        assertThat(savedBook.getCategory()).isEqualTo(category);
        assertThat(savedBook.getPrice()).isEqualTo(1000L);
        assertThat(savedBook.getDiscount()).isEqualTo(0.1f);
        assertThat(savedBook.getLanguage()).isEqualTo(language);
        assertThat(savedBook.getWeight()).isEqualTo(100);
        assertThat(savedBook.getSize()).isEqualTo("size");
        assertThat(savedBook.getStock()).isEqualTo(100);
        assertThat(savedBook.getPublisher()).isEqualTo(publisher);
        assertThat(savedBook.getPublicationDate()).isEqualTo(LocalDate.now());
        List<Author> savedAuthors = savedBook.getAuthors();
        assertThat(savedAuthors).hasSize(2);
        assertThat(savedAuthors).contains(author1, author2);
        List<Image> savedImages = savedBook.getImages();
        assertThat(savedImages).hasSize(1);
        Image savedImage = savedImages.get(0);
        assertThat(savedImage.getLink()).isEqualTo("https://image.com");
        assertThat(savedImage.getDescription()).isEqualTo("description");
        List<BookCollection> savedCollections = savedBook.getCollections();
        assertThat(savedCollections).hasSize(1);
        assertThat(savedCollections).contains(collection);
    }

    @Test
    @Transactional
    void saveBook_DuplicateISBN() {
        List<Author> authors = new ArrayList<Author>();
        var author1 = authorRepository.findById(1L).get();
        var author2 = authorRepository.findById(2L).get();
        var category = categoryRepository.findById(1L).get();
        var collection = collectionRepository.findById(1L).get();
        var language = languageRepository.findById(1L).get();
        var publisher = publisherRepository.findById(1L).get();
        var image = Image.builder().link("https://image.com").description("description").build();
        List<Image> images = new ArrayList<Image>();
        images.add(image);
        List<BookCollection> collections = new ArrayList<BookCollection>();
        collections.add(collection);
        authors.add(author1);
        authors.add(author2);
        var book = Book.builder()
                .title("title")
                .ISBN("978-604-2-23706-2")
                .authors(authors)
                .cover("cover")
                .page(1)
                .description("description")
                .category(category)
                .collections(collections)
                .price(1L)
                .discount(0.5f)
                .language(language)
                .images(images)
                .weight(1)
                .size("size")
                .stock(1)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        ISBNDuplicateException exception = assertThrows(ISBNDuplicateException.class, () -> {
            bookService.saveBook(book);
        });
        assertThat(exception.getMessage()).contains("ISBN can't be duplicated");
    }

    @Test
    @Transactional
    void deleteBook_ExistingBook(){
        bookService.deleteBook(1L);
        var deletedBook = bookRepository.findById(1L).get();
        assertEquals(deletedBook.getState(), BookState.HIDDEN);
    }

    @Test
    @Transactional
    void delete_NotExistingBook() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            bookService.deleteBook(1000L);
        });
        assertThat(exception.getMessage()).contains("No corresponding book found");
    }

    @Test
    @Transactional
    void updateBook_successful() {
        List<Author> authors = new ArrayList<Author>();
        var author1 = authorRepository.findById(1L).get();
        var author2 = authorRepository.findById(2L).get();
        var category = categoryRepository.findById(1L).get();
        var collection = collectionRepository.findById(1L).get();
        var language = languageRepository.findById(1L).get();
        var publisher = publisherRepository.findById(1L).get();
        var image = Image.builder().link("https://image.com").description("description").build();
        List<Image> images = new ArrayList<Image>();
        images.add(image);
        List<BookCollection> collections = new ArrayList<BookCollection>();
        collections.add(collection);
        authors.add(author1);
        authors.add(author2);
        var updateBook = Book.builder()
                .id(1L)
                .title("title")
                .ISBN("978-604-2-26673-8")
                .authors(authors)
                .cover("cover")
                .page(100)
                .description("description")
                .category(category)
                .collections(collections)
                .price(1000L)
                .discount(0.1f)
                .language(language)
                .images(images)
                .weight(100)
                .size("size")
                .stock(100)
                .publisher(publisher)
                .collections(collections)
                .build();
                
        bookService.updateBook(updateBook);

        var savedBook = bookService.getBookById(1L);
        assertEquals(savedBook.getId(), 1L);
        assertThat(savedBook.getTitle()).isEqualTo("title");
        assertThat(savedBook.getISBN()).isEqualTo("978-604-2-26673-8");
        assertThat(savedBook.getCover()).isEqualTo("cover");
        assertThat(savedBook.getPage()).isEqualTo(100);
        assertThat(savedBook.getDescription()).isEqualTo("description");
        assertThat(savedBook.getCategory()).isEqualTo(category);
        assertThat(savedBook.getPrice()).isEqualTo(1000L);
        assertThat(savedBook.getDiscount()).isEqualTo(0.1f);
        assertThat(savedBook.getLanguage()).isEqualTo(language);
        assertThat(savedBook.getWeight()).isEqualTo(100);
        assertThat(savedBook.getSize()).isEqualTo("size");
        assertThat(savedBook.getStock()).isEqualTo(100);
        assertThat(savedBook.getPublisher()).isEqualTo(publisher);
        List<Author> savedAuthors = savedBook.getAuthors();
        assertThat(savedAuthors).hasSize(2);
        assertThat(savedAuthors).contains(author1, author2);
        List<Image> savedImages = savedBook.getImages();
        assertThat(savedImages).hasSize(1);
        Image savedImage = savedImages.get(0);
        assertThat(savedImage.getLink()).isEqualTo("https://image.com");
        assertThat(savedImage.getDescription()).isEqualTo("description");
        List<BookCollection> savedCollections = savedBook.getCollections();
        assertThat(savedCollections).hasSize(1);
        assertThat(savedCollections).contains(collection);
    }

}
