package ru.practicum.shareit.exception;

public class InternalServerErrorException extends Exception {

    public InternalServerErrorException() {
    }

    public InternalServerErrorException(final String message) {
        super(message);
    }

}
