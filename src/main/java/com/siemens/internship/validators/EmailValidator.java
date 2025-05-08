package com.siemens.internship.validators;

import org.yaml.snakeyaml.events.Event;

public class EmailValidator implements Validator<String> {

    @Override
    public void validate(String email) throws ValidationException {
        // creating a string builder to store the errors
        StringBuilder errors = new StringBuilder();

        // checking first if the email is null or empty
        if(email == null || email.isEmpty()){
            errors.append("Email can`t be null or empty.\n");
        }
        else{

            // checking if the email contains only one '@' symbol
            String[] parts = email.split("@");
            if (parts.length != 2) {
                errors.append("Email must contain exactly one '@' symbol.\n");
            }else {
                // verifying that the parts before and after '@' are not empty
                if (parts[0].isEmpty()) {
                    errors.append("Local part (before '@') can't be empty.\n");
                }
                if (parts[1].isEmpty()) {
                    errors.append("Domain part (after '@') can't be empty.\n");
                }
            }

            // regex for validating the email
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

            // checking if the email matches the regex
            if(!email.matches(emailRegex)){
                errors.append("Email format is not valid.\n");
            }
        }

        // if there are errors, throwing a ValidationException
        if(!errors.isEmpty()) {
            throw new ValidationException(errors.toString());
        }
    }
}
