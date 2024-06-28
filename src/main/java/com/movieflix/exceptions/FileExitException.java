package com.movieflix.exceptions;

public class FileExitException extends RuntimeException {
    public FileExitException(String message) {
        super(message);
    }
}
