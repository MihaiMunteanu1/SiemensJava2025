package com.siemens.internship.service;

import com.siemens.internship.repository.ItemRepository;
import com.siemens.internship.model.Item;
import com.siemens.internship.validators.EmailValidator;
import com.siemens.internship.validators.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private List<Item> processedItems = new ArrayList<>();

    // using AtomicInteger for thread-safe increment
    private AtomicInteger processedCount = new AtomicInteger(0);

    // adding email validator
    private final EmailValidator emailValidator;

    public ItemService() {
        this.emailValidator = new EmailValidator();
    }


    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     *
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {
        // Using a thread-safe collection for processed items
        List<Item> processedItems = new CopyOnWriteArrayList<>();

        // Fetching all item IDs
        List<Long> itemIds = itemRepository.findAllIds();

        // Creating a list of CompletableFutures for processing items
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Long id : itemIds) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Item item = itemRepository.findById(id).orElse(null);
                    if (item != null) {
                        // Validating email
                        emailValidator.validate(item.getEmail());

                        // Updating item status and saving
                        item.setStatus("PROCESSED");
                        itemRepository.save(item);

                        // Adding to the thread-safe list and incrementing the counter
                        processedItems.add(item);
                        processedCount.incrementAndGet();
                    } else {
                        System.out.println("Item with id: " + id + " not found.");
                    }
                } catch (ValidationException e) {
                    System.out.println("Invalid email for item with id: " + id + ": " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Error processing item with id: " + id + ": " + e.getMessage());
                }
            }, executor);
            futures.add(future);
        }

        // Waiting for all tasks to complete and returning the processed items
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> processedItems)
                .exceptionally(ex -> {
                    System.err.println("Error during processing: " + ex.getMessage());
                    throw new CompletionException(ex);
                });
    }
//    @Async
//    public CompletableFuture<List<Item>> processItemsAsync() {
//
//        // using a thread-safe collection for processed items
//        List<Item> threadSafeProcessedItems = new CopyOnWriteArrayList<>();
//
//        // creating a list of CompletableFutures for processing items
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//
//
//        List<Long> itemIds = itemRepository.findAllIds();
//
//        for (Long id : itemIds) {
//            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                try {
//                    Thread.sleep(100);
//
//                    Item item = itemRepository.findById(id).orElse(null);
//                    if (item != null) {
//                        // validating email using the EmailValidator
//                        try {
//                            emailValidator.validate(item.getEmail());
//                        } catch (ValidationException e) {
//                            System.out.println("Invalid email for item with id: " + item.getId() + ": " + e.getMessage());
//                            return; // skip processing this item
//                        }
//                        item.setStatus("PROCESSED");
//                        itemRepository.save(item);
//                        processedItems.add(item);
//                        processedCount.incrementAndGet();
//
//                    } else {
//                        System.out.println("Item with id: " + id + " not found.");
//                        return; // skip processing if item is not found
//                    }
//
//                } catch (InterruptedException e) {
//                    System.out.println("Error: " + e.getMessage());
//                }
//            }, executor);
//            futures.add(future);
//        }
//
//        // waiting for all tasks to complete and return the processed items
//        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
//                .thenApply(v -> threadSafeProcessedItems)
//                .exceptionally(ex -> {
//                    System.err.println("Error during processing: " + ex.getMessage());
//                    throw new CompletionException(ex);
//                });
//    }

}

