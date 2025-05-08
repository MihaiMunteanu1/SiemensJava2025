package com.siemens.internship;

import com.siemens.internship.model.Item;
import com.siemens.internship.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RepositoryTest {
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void repoTest() {
        Item item = new Item(1L, "a", "a", "PROCESSED", "a@yahoo.com");
        Item saved = itemRepository.save(item);

        assertEquals(saved.getId(), item.getId());
        assertNotNull(saved.getId());

        Item found = itemRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(item.getName(), found.getName());
        assertEquals(item.getDescription(), found.getDescription());

        item.setName("updated");
        Item updated = itemRepository.save(item);
        assertEquals("updated", updated.getName());

        itemRepository.deleteById(saved.getId());
        Item deleted = itemRepository.findById(saved.getId()).orElse(null);
        assertNull(deleted);
    }

}
