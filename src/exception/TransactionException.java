package exception;

public class TransactionException extends RuntimeException{

    public TransactionException(){
        super("Сумма транзакции слишком велика , либо мала!");
    }

}