package com.siemens.internship;

import com.siemens.internship.controller.ItemController;
import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import com.siemens.internship.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ControllerTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemController itemController;

    @Test
    void controllerTest() throws ExecutionException, InterruptedException {
        testCreateItem();
        testGetAllItems();
        testGetItemById();
        testUpdateItem();
        testDeleteItem();
        testProcessItems();
    }

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
    }

    @Test
    void testGetAllItems() {
        Item item1 = new Item(null, "Item1", "Description1", "NEW", "item1@example.com");
        Item item2 = new Item(null, "Item2", "Description2", "NEW", "item2@example.com");
        itemService.save(item1);
        itemService.save(item2);

        ResponseEntity<List<Item>> response = itemController.getAllItems();
        assertEquals(2, response.getBody().size());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testCreateItem() {
        Item item = new Item(null, "Item1", "Description1", "NEW", "item1@example.com");
        ResponseEntity<Item> response = itemController.createItem(item, null);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void testGetItemById() {
        Item item = new Item(null, "Item1", "Description1", "NEW", "item1@example.com");
        Item savedItem = itemService.save(item);

        ResponseEntity<Item> response = itemController.getItemById(savedItem.getId());
        assertTrue(response.getBody() != null && response.getBody().getId().equals(savedItem.getId()));
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testUpdateItem() {
        Item item = new Item(null, "Item1", "Description1", "NEW", "item1@example.com");
        Item savedItem = itemService.save(item);

        Item updatedItem = new Item(null, "UpdatedItem", "UpdatedDescription", "UPDATED", "updated@example.com");
        ResponseEntity<Item> response = itemController.updateItem(savedItem.getId(), updatedItem);

        assertEquals("UpdatedItem", response.getBody().getName());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testDeleteItem() {
        Item item = new Item(null, "Item1", "Description1", "NEW", "item1@example.com");
        Item savedItem = itemService.save(item);

        ResponseEntity<Void> response = itemController.deleteItem(savedItem.getId());
        assertEquals(204, response.getStatusCodeValue());
        assertFalse(itemService.findById(savedItem.getId()).isPresent());
    }

    @Test
    void testProcessItems() throws ExecutionException, InterruptedException {
        Item item1 = new Item(null, "Item1", "Description1", "NEW", "item1@example.com");
        Item item2 = new Item(null, "Item2", "Description2", "NEW", "item2@example.com");
        itemService.save(item1);
        itemService.save(item2);

        ResponseEntity<List<Item>> response = itemController.processItems();
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().allMatch(item -> "PROCESSED".equals(item.getStatus())));
        assertEquals(200, response.getStatusCodeValue());
    }


}
