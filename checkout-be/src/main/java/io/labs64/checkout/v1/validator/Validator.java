package io.labs64.checkout.v1.validator;

public interface Validator<T> {
    void validate(T entity);
}