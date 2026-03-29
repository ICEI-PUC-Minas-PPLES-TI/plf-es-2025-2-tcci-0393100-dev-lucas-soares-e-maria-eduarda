package br.pucminas.graphtest.application.exception;

public class InvalidGceModelException extends ConflictException
{
    public InvalidGceModelException(String message) {
        super(message);
    }
}
