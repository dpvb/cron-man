package exceptions;

public class CronParseException extends RuntimeException {

    public CronParseException(String message) {
        super(message);
    }

}
