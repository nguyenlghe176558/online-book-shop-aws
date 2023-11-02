package com.kas.online_book_shop;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kas.online_book_shop.enums.AccountState;
import com.kas.online_book_shop.enums.Role;
import com.kas.online_book_shop.model.User;
import com.kas.online_book_shop.repository.UserRepository;

import jakarta.validation.ConstraintViolationException;
import lombok.val;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void testFindUser() {
        // Create a test user with all required properties
        User testUser = new User();
        testUser.setFullName("John Doesalot");
        testUser.setProvince("Test Province");
        testUser.setDistrict("Test District");
        testUser.setWard("Test Ward");
        testUser.setAddress("Test Address");
        testUser.setPhone("0123456789");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("testpassword");
        testUser.setRole(Role.USER);
        testUser.setState(AccountState.ACTIVE);

        // Save the test user to the database
        userRepository.save(testUser);

        // Search for the user by full name and state
        Page<User> foundUsers = userRepository.findByFullNameContainingAndRoleAndState("John Doesalot", null,
                AccountState.ACTIVE, PageRequest.of(0, 10));

        // Assert that the user is found
        assertEquals(1, foundUsers.getTotalElements());
    }

    @Test
    @Transactional
    public void testFindUserByFullNameAndStateNotFound() {
        // Search for a user that doesn't exist in the database
        Page<User> foundUsers = userRepository.findByFullNameContainingAndRoleAndState("NonExistentUser", null,
                AccountState.ACTIVE, PageRequest.of(0, 10));

        // Assert that no user is found
        assertEquals(0, foundUsers.getTotalElements());
    }

    @Test
    @Transactional
    public void testFindByEmail() {
        // Create a test user
        User testUser = new User();
        testUser.setFullName("Alice Wonderland");
        testUser.setProvince("Test Province");
        testUser.setDistrict("Test District");
        testUser.setWard("Test Ward");
        testUser.setAddress("Test Address");
        testUser.setPhone("0123456789");
        testUser.setEmail("alice@example.com");
        testUser.setPassword("testpassword");
        testUser.setRole(Role.USER);
        testUser.setState(AccountState.ACTIVE);
        userRepository.save(testUser);

        // Search for the user by email
        Optional<User> foundUser = userRepository.findByEmail("alice@example.com");

        // Assert that the user is found
        assertTrue(foundUser.isPresent());
        assertEquals("Alice Wonderland", foundUser.get().getFullName());
    }

    @Test
    @Transactional
    public void testFindByEmailNotFound() {
        // Search for a user by an email that doesn't exist in the database
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert that no user is found
        assertTrue(foundUser.isEmpty());
    }

    @Test
    @Transactional
    public void testInsertUserWithAllProperties() {
        // Create a test user with all required properties
        User testUser = new User();
        testUser.setFullName("John Doe");
        testUser.setProvince("Test Province");
        testUser.setDistrict("Test District");
        testUser.setWard("Test Ward");
        testUser.setAddress("Test Address");
        testUser.setPhone("0123456789");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("testpassword");
        testUser.setRole(Role.USER);
        testUser.setState(AccountState.ACTIVE);

        // Save the test user to the database
        User savedUser = userRepository.save(testUser);

        // Retrieve the saved user from the database
        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Assert that the user is not null
        assertNotNull(retrievedUser);

        // Assert that the properties match what was saved
        assertEquals("John Doe", retrievedUser.getFullName());
        assertEquals("Test Province", retrievedUser.getProvince());
        assertEquals("Test District", retrievedUser.getDistrict());
        assertEquals("Test Ward", retrievedUser.getWard());
        assertEquals("Test Address", retrievedUser.getAddress());
        assertEquals("0123456789", retrievedUser.getPhone());
        assertEquals("testuser@example.com", retrievedUser.getEmail());
        assertEquals("testpassword", retrievedUser.getPassword());
        assertEquals(Role.USER, retrievedUser.getRole());
        assertEquals(AccountState.ACTIVE, retrievedUser.getState());
    }

    @Test
    @Transactional
    public void testDuppliateEmail() {
        // insert user 1
        User testUser = new User();
        testUser.setFullName("John Doe");
        testUser.setProvince("Test Province");
        testUser.setDistrict("Test District");
        testUser.setWard("Test Ward");
        testUser.setAddress("Test Address");
        testUser.setPhone("0942536475");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("testpassword");
        testUser.setRole(Role.USER);
        testUser.setState(AccountState.ACTIVE);

        // insert user 2
        User testUser2 = new User();
        testUser.setFullName("John Doe2");
        testUser.setProvince("Test Province2");
        testUser.setDistrict("Test District2");
        testUser.setWard("Test Ward2");
        testUser.setAddress("Test Address2");
        testUser.setPhone("0942536475");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("testpassword");
        testUser.setRole(Role.USER);
        testUser.setState(AccountState.ACTIVE);

        // save to database
        userRepository.save(testUser);

        // Attempt to save the second user with the same email and catch the expected
        // exception
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            userRepository.save(testUser2);
        });

        // Verify that the exception message contains a specific error message
        assertThat(exception.getMessage().contains("The email is invalid"));
    }

    @Test
    @Transactional
    public void testMissingFullName() {
        // insert user 1
        var testUser = User.builder()
                .fullName("")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("Test Province")
                .district("Test District")
                .ward("Test Ward")
                .phone("0943546576")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            // save to database
            userRepository.save(testUser);
        });

        // Verify that the exception message contains a specific error message
        assertThat(exception.getMessage().contains("The full name is required"));
    }

    @Test
    @Transactional
    public void testMissingProvince() {
        // insert user 1
        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("")
                .district("Test District")
                .ward("Test Ward")
                .phone("0943546576")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            // save to database
            userRepository.save(testUser);
        });

        // Verify that the exception message contains a specific error message
        assertThat(exception.getMessage().contains("The province is required"));
    }

    @Test
    @Transactional
    public void testMissingDistrict() {
        // insert user 1
        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("Test Province")
                .district("")
                .ward("Test Ward")
                .phone("0943546576")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            // save to database
            userRepository.save(testUser);
        });

        // Verify that the exception message contains a specific error message
        assertThat(exception.getMessage().contains("The district is required"));
    }

    @Test
    @Transactional
    public void testMissingWard() {
        // insert user 1
        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("Test Province")
                .district("Test District")
                .ward("")
                .phone("0943546576")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            // save to database
            userRepository.save(testUser);
        });

        // Verify that the exception message contains a specific error message
        assertThat(exception.getMessage().contains("The ward is required"));
    }

    @Test
    @Transactional
    public void testMissingAddress() {
        // insert user 1
        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("Test Province")
                .district("Test District")
                .ward("")
                .phone("0943546576")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            // save to database
            userRepository.save(testUser);
        });

        // Verify that the exception message contains a specific error message
        assertThat(exception.getMessage().contains("The address is required"));
    }

    @Test
    @Transactional
    public void testMissingPhone() {
        // insert user 1
        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("Test Province")
                .district("Test District")
                .ward("TEST WARD")
                .phone("")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            // save to database
            userRepository.save(testUser);
        });

        // Verify that the exception message contains a specific error message
        assertThat(exception.getMessage().contains("The phone is required"));
    }

    @Test
    @Transactional
    public void testInvalidPhone() {
        // insert user 1
        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("Test Province")
                .district("Test District")
                .ward("TEST WARD")
                .phone("1234567898")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            // save to database
            userRepository.save(testUser);
        });

        // Verify that the exception message contains a specific error message
        assertThat(exception.getMessage().contains("The phone is invalid"));
    }

    @Test
    @Transactional
    public void testInvalidBoundariesFullName() {
        // Create a user with a full name that exceeds the maximum length (255
        // characters)
        String fullName = "A".repeat(256); // 256 characters, exceeding the limit

        var testUser = User.builder()
                .fullName(fullName)
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("Test Province")
                .district("Test District")
                .ward("TEST WARD")
                .phone("0943546576")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> {
                    // Attempt to save the user to the database
                    userRepository.save(testUser);
                });

        // Verify that the exception message contains a specific error message about the
        // full name length
        assertThat(exception.getMessage()).contains("The full name must be between 1 and 255 characters'");
    }

    @Test
    @Transactional
    public void testInvalidBoundariesProvince() {
        // Create a user with a property that exceeds the maximum length (255
        // characters)
        String province = "A".repeat(256); // 256 characters, exceeding the limit

        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province(province)
                .district("Test District")
                .ward("TEST WARD")
                .phone("0943546576")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> {
                    // Attempt to save the user to the database
                    userRepository.save(testUser);
                });

        // Verify that the exception message contains a specific error message about the
        // full length
        assertThat(exception.getMessage()).contains("The province must be between 1 and 255 characters");
    }

    @Test
    @Transactional
    public void testInvalidBoundariesDistrict() {
        // Create a user with a property that exceeds the maximum length (255
        // characters)
        String test = "A".repeat(256); // 256 characters, exceeding the limit

        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("province")
                .district(test)
                .ward("TEST WARD")
                .phone("0943546576")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> {
                    // Attempt to save the user to the database
                    userRepository.save(testUser);
                });

        // Verify that the exception message contains a specific error message about the
        // full length
        assertThat(exception.getMessage()).contains("The district must be between 1 and 255 characters");
    }

    @Test
    @Transactional
    public void testInvalidBoundariesWard() {
        // Create a user with a property that exceeds the maximum length (255
        // characters)
        String test = "A".repeat(256); // 256 characters, exceeding the limit

        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("province")
                .district("test")
                .ward(test)
                .phone("0943546576")
                .address("test address")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> {
                    // Attempt to save the user to the database
                    userRepository.save(testUser);
                });

        // Verify that the exception message contains a specific error message about the
        // full length
        assertThat(exception.getMessage()).contains("The ward must be between 1 and 255 characters");
    }

    @Test
    @Transactional
    public void testInvalidBoundariesAddress() {
        // Create a user with a property that exceeds the maximum length (255
        // characters)
        String test = "A".repeat(256); // 256 characters, exceeding the limit

        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("province")
                .district("test")
                .ward("test")
                .phone("0943546576")
                .address(test)
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> {
                    // Attempt to save the user to the database
                    userRepository.save(testUser);
                });

        // Verify that the exception message contains a specific error message about the
        // full length
        assertThat(exception.getMessage()).contains("The address must be between 1 and 255 characters");
    }

    @Test
    @Transactional
    public void testInvalidBoundariesPhone() {
        // Create a user with a property that exceeds the maximum length (255
        // characters)
        String test = "094" + "8".repeat(8); 

        var testUser = User.builder()
                .fullName("Lana Del Rey")
                .email("test@example.com")
                .password("testpassword")
                .role(Role.USER)
                .state(AccountState.INACTIVE)
                .province("province")
                .district("test")
                .ward("test")
                .phone(test)
                .address("test")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class,
                () -> {
                    // Attempt to save the user to the database
                    userRepository.save(testUser);
                });

        // Verify that the exception message contains a specific error message about the
        // full length
        assertThat(exception.getMessage()).contains("The phone number must be 10 characters");
    }
}
