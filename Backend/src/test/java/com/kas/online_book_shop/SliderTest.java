package com.kas.online_book_shop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kas.online_book_shop.model.Slider;
import com.kas.online_book_shop.repository.SliderRepository;
import com.kas.online_book_shop.service.SliderServiceImpl;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SliderTest {

    @Autowired
    private SliderServiceImpl sliderService;

    @Autowired
    private SliderRepository sliderRepository;

    @Test
    public void testCreateSlider() {
        // Database already has 3 sliders
        // Insert test data into the actual database
        Slider slider1 = new Slider();
        slider1.setTitle("Slider 1");
        slider1.setDescription("Description 1");
        slider1.setImageUrl("https://www.google.com");
        slider1.setBackLink("https://www.google.com");
        sliderRepository.save(slider1);

        Slider slider2 = new Slider();
        slider2.setTitle("Slider 2");
        slider2.setDescription("Description 2");
        slider2.setImageUrl("https://www.google.com");
        slider2.setBackLink("https://www.google.com");
        sliderRepository.save(slider2);

        // Initialize the service manually
        sliderService = new SliderServiceImpl(sliderRepository);

        // Call the method you want to test
        var retrievedSliders = sliderService.getAllSliders();

        // Verify the result
        assertNotNull(retrievedSliders);
        assertEquals(5, retrievedSliders.size());
    }

    @Test
    public void testGetAllSliders() {
        // Initialize the service manually
        sliderService = new SliderServiceImpl(sliderRepository);

        // Call the method you want to test
        var retrievedSliders = sliderService.getAllSliders();

        // Verify the result
        assertNotNull(retrievedSliders);
        assertEquals(3, retrievedSliders.size());
    }

    @Test
    public void testDeleteSlider() {
        // Initialize the service manually
        sliderService = new SliderServiceImpl(sliderRepository);

        // Call the method you want to test
        sliderService.deleteSlider(1L);

        // Verify the result
        assertEquals(2, sliderRepository.count());
    }

    @Test
    public void testUpdateSlider() {
        // Initialize the service manually
        sliderService = new SliderServiceImpl(sliderRepository);

        // Call the method you want to test
        var slider = sliderService.getSliderById(1L);
        slider.setTitle("Updated title");
        sliderService.updateSlider(slider);

        // Verify the result
        assertEquals("Updated title", sliderRepository.findById(1L).get().getTitle());
    }

    @Test
    public void testGetSliderById() {
        // Initialize the service manually
        sliderService = new SliderServiceImpl(sliderRepository);

        // Call the method you want to test
        var slider = sliderService.getSliderById(1L);

        // Verify the result
        assertNotNull(slider);
        assertEquals("Slider 1", slider.getTitle());
    }

    @Test
    public void testNonExistingSlider() {
        // Initialize the service manually
        sliderService = new SliderServiceImpl(sliderRepository);

        // Call the method you want to test
        var slider = sliderService.getSliderById(100L);

        // Verify the result
        assertEquals(null, slider);
    }
}
