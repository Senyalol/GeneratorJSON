import lombok.Data;

@Data
public class User {

    private Integer user_id;
    private String firstname;
    private String lastname;

    public User(Integer user_id, String firstname, String lastname) {
        this.user_id = user_id;
        this.firstname = firstname;
        this.lastname = lastname;
    }

}