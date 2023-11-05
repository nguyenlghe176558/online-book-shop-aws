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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.enums.BookState;
import com.kas.online_book_shop.model.Author;
import com.kas.online_book_shop.model.Book;
import com.kas.online_book_shop.model.BookCategory;
import com.kas.online_book_shop.model.BookCollection;
import com.kas.online_book_shop.model.Image;
import com.kas.online_book_shop.repository.AuthorRepository;
import com.kas.online_book_shop.repository.BookCategoryRepository;
import com.kas.online_book_shop.repository.BookCollectionRepository;
import com.kas.online_book_shop.repository.BookRepository;
import com.kas.online_book_shop.repository.LanguageRepository;
import com.kas.online_book_shop.repository.PublisherRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

@SpringBootTest
@ActiveProfiles("test")
public class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private BookCollectionRepository bookCollectionRepository;

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
    @Test
    @Transactional
    /* 
     * full normal value
     */
    void save_valid() {
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
                .ISBN("978-604-12-2663-8")
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
                .sold(1)
                .build();
        var savedBook = bookRepository.save(book);
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("title");
        assertThat(savedBook.getISBN()).isEqualTo("978-604-12-2663-8");
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
        assertThat(savedBook.getSold()).isEqualTo(1);
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
    /* 
     * save boundaries 1 
     */
    void save_valid_2() {
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
                .title("t")
                .ISBN("978-604-12-2663-8")
                .authors(authors)
                .cover("T".repeat(255))
                .page(1)
                .description("")
                .category(category)
                .collections(collections)
                .price(1L)
                .discount(1f)
                .language(language)
                .images(images)
                .weight(100)
                .size("T".repeat(255))
                .sold(0)
                .stock(100)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        var savedBook = bookRepository.save(book);
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("t");
        assertThat(savedBook.getISBN()).isEqualTo("978-604-12-2663-8");
        assertThat(savedBook.getCover()).isEqualTo("T".repeat(255));
        assertThat(savedBook.getPage()).isEqualTo(1);
        assertThat(savedBook.getDescription()).isEqualTo("");
        assertThat(savedBook.getCategory()).isEqualTo(category);
        assertThat(savedBook.getPrice()).isEqualTo(1L);
        assertThat(savedBook.getDiscount()).isEqualTo(1.0f);
        assertThat(savedBook.getLanguage()).isEqualTo(language);
        assertThat(savedBook.getWeight()).isEqualTo(100);
        assertThat(savedBook.getSize()).isEqualTo("T".repeat(255));
        assertThat(savedBook.getStock()).isEqualTo(100);
        assertThat(savedBook.getSold()).isEqualTo(0);
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
    /* 
     * save boundaries 2
     */
    void save_valid_3() {
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
                .title("T".repeat(255))
                .ISBN("978-604-12-2663-8")
                .authors(authors)
                .cover("T".repeat(255))
                .page(1)
                .description("")
                .category(category)
                .collections(collections)
                .price(1000L)
                .discount(0f)
                .language(language)
                .images(images)
                .weight(100)
                .size("T".repeat(255))
                .stock(100)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        var savedBook = bookRepository.save(book);
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("T".repeat(255));
        assertThat(savedBook.getISBN()).isEqualTo("978-604-12-2663-8");
        assertThat(savedBook.getCover()).isEqualTo("T".repeat(255));
        assertThat(savedBook.getPage()).isEqualTo(1);
        assertThat(savedBook.getDescription()).isEqualTo("");
        assertThat(savedBook.getCategory()).isEqualTo(category);
        assertThat(savedBook.getPrice()).isEqualTo(1000L);
        assertThat(savedBook.getDiscount()).isEqualTo(0f);
        assertThat(savedBook.getLanguage()).isEqualTo(language);
        assertThat(savedBook.getWeight()).isEqualTo(100);
        assertThat(savedBook.getSize()).isEqualTo("T".repeat(255));
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
    void save_TitleEmpty() {
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
                .title("")
                .ISBN("978-604-12-2663-8")
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
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The title of the book is required");
    }

    @Test
    @Transactional
    void save_TitleMoreThan255chars() {
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
                .title("T".repeat(256))
                .ISBN("978-604-12-2663-8")
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
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The length is more than 255 characters");
    }

    @Test
    @Transactional
    void save_ISBNEmpty() {
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
                .title("")
                .ISBN("")
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
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The ISBN is required");
    }


    @Test
    @Transactional
    void save_ISBNInvalidPattern() {
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
                .title("The title")
                .ISBN("123133-131313")
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
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The ISBN is invalid");
    }

    @Test
    @Transactional
    void save_StockLessThan0() {
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
                .ISBN("978-604-12-2663-8")
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
                .stock(-1)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("stock must be at least 0");
    }

    @Test
    @Transactional
    void save_SoldLessThan0() {
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
                .ISBN("978-604-12-2663-8")
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
                .stock(1)
                .sold(-1)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("Sold must be at least 0");
    }

    @Test
    @Transactional
    void save_WeightLessThan1() {
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
                .ISBN("stock must be at least 0")
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
                .weight(0)
                .size("size")
                .stock(100)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The weight must be at least 1");
    }

    @Test
    @Transactional
    void save_PriceLessThan1() {
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
                .ISBN("978-604-12-2663-8")
                .authors(authors)
                .cover("cover")
                .page(100)
                .description("description")
                .category(category)
                .collections(collections)
                .price(0L)
                .discount(0.1f)
                .language(language)
                .images(images)
                .weight(1)
                .size("size")
                .stock(0)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The price must be at least 1");
    }

    @Test
    @Transactional
    void save_PageLessThan1() {
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
                .ISBN("978-604-12-2663-8")
                .authors(authors)
                .cover("cover")
                .page(-1)
                .description("description")
                .category(category)
                .collections(collections)
                .price(1L)
                .discount(0.1f)
                .language(language)
                .images(images)
                .weight(1)
                .size("size")
                .stock(1)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The page must be at least 1");
    }

    @Test
    @Transactional
    void save_DiscountLessThan0() {
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
                .ISBN("978-604-12-2663-8")
                .authors(authors)
                .cover("cover")
                .page(1)
                .description("description")
                .category(category)
                .collections(collections)
                .price(1L)
                .discount(-0.1f)
                .language(language)
                .images(images)
                .weight(1)
                .size("size")
                .stock(1)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The discount must be at least 0");
    }

    @Test
    @Transactional
    void save_DiscountMoreThan1() {
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
                .ISBN("978-604-12-2663-8")
                .authors(authors)
                .cover("cover")
                .page(1)
                .description("description")
                .category(category)
                .collections(collections)
                .price(1L)
                .discount(1.1f)
                .language(language)
                .images(images)
                .weight(1)
                .size("size")
                .stock(1)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The discount must be equal and less than 1");
    }

    @Test
    @Transactional
    void save_DuplicateISBN() {
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
        assertThrows(DataIntegrityViolationException.class, () -> {
            bookRepository.save(book);
        });
    }
    

    @Test
    @Transactional
    void save_CoverMoreThan255chars() {
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
                .title("T")
                .ISBN("978-604-12-2663-8")
                .authors(authors)
                .cover("T".repeat(256))
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
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The length is more than 255 characters");
    }

    @Test
    @Transactional
    void save_SizeMoreThan255chars() {
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
                .title("T")
                .ISBN("978-604-12-2663-8")
                .authors(authors)
                .cover("")
                .page(100)
                .description("description")
                .category(category)
                .collections(collections)
                .price(1000L)
                .discount(0.1f)
                .language(language)
                .images(images)
                .weight(100)
                .size("T".repeat(256))
                .stock(100)
                .publisher(publisher)
                .publicationDate(LocalDate.now())
                .collections(collections)
                .build();
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            bookRepository.save(book);
        });
        assertThat(exception.getMessage()).contains("The length is more than 255 characters");
    }
}