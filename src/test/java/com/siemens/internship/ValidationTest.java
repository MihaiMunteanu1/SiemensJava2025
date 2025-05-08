package com.siemens.internship;

import com.siemens.internship.model.Item;
import com.siemens.internship.validators.EmailValidator;
import com.siemens.internship.validators.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ValidationTest {


    @Test
    void validationTest(){
        EmailValidator emailValidator = new EmailValidator();

        Item item1 = new Item(1L, "a", "a", "NEW", "a@yahoo.com");
        Item item2 = new Item(2L, "a", "a", "NEW", "a@yah@oo.com");
        Item item3 = new Item(3L, "a", "a", "NEW", "");
        Item item4 = new Item(4L, "a", "a", "NEW", "a@yahoo");
        Item item5 = new Item(5L, "a", "a", "NEW", "@yahoo.com");
        Item item6 = new Item(6L, "a", "a", "NEW", "a@");

        // valid email
        assertDoesNotThrow(() -> emailValidator.validate(item1.getEmail()));

        // invalid emails
        assertThrows(ValidationException.class, () -> emailValidator.validate(item2.getEmail()));
        assertThrows(ValidationException.class, () -> emailValidator.validate(item3.getEmail()));
        assertThrows(ValidationException.class, () -> emailValidator.validate(item4.getEmail()));
        assertThrows(ValidationException.class, () -> emailValidator.validate(item5.getEmail()));
        assertThrows(ValidationException.class, () -> emailValidator.validate(item6.getEmail()));


    }

}
