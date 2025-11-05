package io.labs64.checkout.validator;

public interface Validator<T> {
    void validate(T entity);
}