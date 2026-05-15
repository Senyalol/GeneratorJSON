package exception;

public class GenerateNullException extends RuntimeException {

    public GenerateNullException(Object o) {
        super("Object: " + o + "is null");
    }

}