// package com.kas.online_book_shop;

// import static org.assertj.core.api.Assertions.assertThat;
// import static org.junit.jupiter.api.Assertions.assertThrows;

// import java.util.List;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;

// import com.kas.online_book_shop.model.Author;
// import com.kas.online_book_shop.repository.AuthorRepository;

// import jakarta.transaction.Transactional;
// import jakarta.validation.ConstraintViolationException;

// @SpringBootTest
// @ActiveProfiles("test")
// public class AuthorRepositoryTest {
//     @Autowired
//     private AuthorRepository authorRepository;

//     @Test
//     @Transactional
//     void addValidAuthor() {
//         long currentNumberAuthor = authorRepository.count();
//         var author = Author.builder().name("tac gia").company("company").build();
//         authorRepository.save(author);
//         assertThat(authorRepository.count()).isEqualTo(currentNumberAuthor + 1);
//         var newAuthor = authorRepository.findAll().get((int) currentNumberAuthor);
//         assertThat(newAuthor.getName()).isEqualTo("tac gia");
//         assertThat(newAuthor.getCompany()).isEqualTo("company");
//     }

//     @Test
//     @Transactional
//     void addInvalidAuthorWithEmptyName() {
//         long currentNumberAuthor = authorRepository.count();
//         ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
//             Author author = Author.builder().name("").company("company").build();
//             authorRepository.save(author);
//             assertThat(authorRepository.count()).isEqualTo(currentNumberAuthor);
//         });
//         assertThat(exception.getMessage()).contains("The author name is required");
//     }

//     @Test
//     @Transactional
//     void updateAuthor() {
//         Author author = Author.builder().name("Author1").company("Company1").build();
//         authorRepository.save(author);
//         long currentNumberAuthor = authorRepository.count();

//         // Update the author's name and company
//         Author updatedAuthor = authorRepository.findById(author.getId()).orElse(null);
//         updatedAuthor.setName("Updated Author");
//         updatedAuthor.setCompany("Updated Company");
//         authorRepository.save(updatedAuthor);

//         // Verify that the author's information has been updated
//         Author retrievedAuthor = authorRepository.findById(author.getId()).orElse(null);
//         assertThat(retrievedAuthor.getName()).isEqualTo("Updated Author");
//         assertThat(retrievedAuthor.getCompany()).isEqualTo("Updated Company");
//         assertThat(authorRepository.count()).isEqualTo(currentNumberAuthor);
//     }

//     @Test
//     @Transactional
//     void findAuthorByName() {
//         Author author1 = Author.builder().name("Author3").company("Company3").build();
//         Author author2 = Author.builder().name("Author4").company("Company4").build();
//         authorRepository.saveAll(List.of(author1, author2));

//         // Find authors by name
//         List<Author> foundAuthors = authorRepository.findByName("Author3");

//         // Verify that the correct author(s) with the specified name are found
//         assertThat(foundAuthors).hasSize(1);
//         assertThat(foundAuthors.get(0).getName()).isEqualTo("Author3");
//         assertThat(foundAuthors.get(0).getCompany()).isEqualTo("Company3");
//     }

//     @Test
//     @Transactional
//     void findAuthorsByCompany() {
//         Author author1 = Author.builder().name("Author7").company("Company7").build();
//         Author author2 = Author.builder().name("Author8").company("Company8").build();
//         authorRepository.saveAll(List.of(author1, author2));

//         // Find authors by company
//         List<Author> authorsWithCompany7 = authorRepository.findByCompany("Company7");

//         // Verify that the correct author(s) with the specified company are found
//         assertThat(authorsWithCompany7).hasSize(1);
//         assertThat(authorsWithCompany7.get(0).getName()).isEqualTo("Author7");
//         assertThat(authorsWithCompany7.get(0).getCompany()).isEqualTo("Company7");
//     }

//     @Test
//     @Transactional
//     void findAuthorsByNameAndCompany() {
//         Author author1 = Author.builder().name("Author9").company("Company9").build();
//         Author author2 = Author.builder().name("Author10").company("Company10").build();
//         authorRepository.saveAll(List.of(author1, author2));

//         // Find authors by name and company
//         List<Author> authorsWithSpecificNameAndCompany = authorRepository.findByNameAndCompany("Author9", "Company9");

//         // Verify that the correct author(s) with the specified name and company are
//         // found
//         assertThat(authorsWithSpecificNameAndCompany).hasSize(1);
//         assertThat(authorsWithSpecificNameAndCompany.get(0).getName()).isEqualTo("Author9");
//         assertThat(authorsWithSpecificNameAndCompany.get(0).getCompany()).isEqualTo("Company9");
//     }
// }
