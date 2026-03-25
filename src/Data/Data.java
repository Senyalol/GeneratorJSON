package Data;

import java.math.BigDecimal;

@lombok.Data
public class Data {

    private Integer user_id;
    private String firstname;
    private String lastname;
    private TransactionType type;
    private BigDecimal sum;
    private long event_time;

}