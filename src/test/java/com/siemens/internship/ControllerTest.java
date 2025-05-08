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
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ControllerTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemController itemController;


    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
    }

    @Test
    void testGetAllItems() {
        Item item1 = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item item2 = new Item(2L, "b", "b", "NEW", "b@gmail.com");
        itemService.save(item1);
        itemService.save(item2);

        ResponseEntity<List<Item>> response = itemController.getAllItems();
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testCreateItem() {
        Item item = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        BindingResult mockBindingResult = mock(BindingResult.class);
        when(mockBindingResult.hasErrors()).thenReturn(false);

        ResponseEntity<Item> response = itemController.createItem(item, mockBindingResult);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    void testGetItemById() {
        Item item = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item savedItem = itemService.save(item);

        ResponseEntity<Item> response = itemController.getItemById(savedItem.getId());
        assertTrue(response.getBody() != null && response.getBody().getId().equals(savedItem.getId()));
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testUpdateItem() {
        Item item = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item savedItem = itemService.save(item);

        Item updatedItem = new Item(2L, "updated", "updated", "UPDATED", "updated@yahoo.com");
        ResponseEntity<Item> response = itemController.updateItem(savedItem.getId(), updatedItem);

        assertNotNull(response.getBody());
        assertEquals("updated", response.getBody().getName());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testDeleteItem() {
        Item item = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item savedItem = itemService.save(item);

        ResponseEntity<Void> response = itemController.deleteItem(savedItem.getId());
        assertEquals(204, response.getStatusCode().value());
        assertFalse(itemService.findById(savedItem.getId()).isPresent());
    }

    @Test
    void testProcessItems() throws ExecutionException, InterruptedException {
        Item item1 = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item item2 = new Item(2L, "b", "b", "NEW", "b@gmail.com");
        itemService.save(item1);
        itemService.save(item2);

        ResponseEntity<List<Item>> response = itemController.processItems();
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().stream().allMatch(item -> "PROCESSED".equals(item.getStatus())));
        assertEquals(200, response.getStatusCode().value());
    }


}
