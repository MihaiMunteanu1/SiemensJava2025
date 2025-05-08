package com.siemens.internship;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ServiceTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;


    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
    }

    @Test
    void testFindAll() {
        Item item1 = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item item2 = new Item(2L, "b", "b", "NEW", "b@gmail.com");
        itemRepository.save(item1);
        itemRepository.save(item2);

        List<Item> items = itemService.findAll();
        assertEquals(2, items.size());
    }

    @Test
    void testFindById() {
        Item item = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item savedItem = itemRepository.save(item);

        Optional<Item> foundItem = itemService.findById(savedItem.getId());
        assertTrue(foundItem.isPresent());
        assertEquals("a", foundItem.get().getName());
    }

    @Test
    void testSave() {
        Item item = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item savedItem = itemService.save(item);

        assertNotNull(savedItem.getId());
        assertEquals("a", savedItem.getName());
    }

    @Test
    void testDeleteById() {
        Item item = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item savedItem = itemRepository.save(item);

        itemService.deleteById(savedItem.getId());
        Optional<Item> deletedItem = itemRepository.findById(savedItem.getId());
        assertFalse(deletedItem.isPresent());
    }

    @Test
    void testProcessItemsAsync() throws Exception {
        Item item1 = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item item2 = new Item(2L, "b", "b", "NEW", "b@gmail.com");
        itemRepository.save(item1);
        itemRepository.save(item2);

        CompletableFuture<List<Item>> future = itemService.processItemsAsync();
        List<Item> processedItems = future.get();

        // checking if all items are processed
        assertEquals(2, processedItems.size());
        assertTrue(processedItems.stream().allMatch(item -> "PROCESSED".equals(item.getStatus())));
    }

}