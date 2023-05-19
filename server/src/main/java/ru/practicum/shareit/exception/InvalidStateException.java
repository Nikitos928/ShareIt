package ru.practicum.shareit.exception;

public class InvalidStateException extends Exception {

    public InvalidStateException() {
    }

    public InvalidStateException(final String message) {
        super(message);
    }

}
