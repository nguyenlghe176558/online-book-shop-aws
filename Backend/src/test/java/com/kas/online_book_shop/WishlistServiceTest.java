package com.kas.online_book_shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.service.WishlistService;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WishlistServiceTest {
    
    @Autowired
    WishlistService wishlistService;

    
}
