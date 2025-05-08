package com.siemens.internship.validators;

@FunctionalInterface
public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
