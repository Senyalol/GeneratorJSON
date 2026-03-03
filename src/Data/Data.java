package Data;

import java.math.BigDecimal;

@lombok.Data
public class Data {

    private Integer user_id;
    private String firstname;
    private String lastname;
    private TransactionType type;
    private BigDecimal sum;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // user_id
        sb.append("\"user_id\":").append(user_id != null ? user_id : "null");

        // firstname
        sb.append(",\"firstname\":");
        if (firstname != null) {
            sb.append("\"").append(escapeJson(firstname)).append("\"");
        } else {
            sb.append("null");
        }

        // lastname
        sb.append(",\"lastname\":");
        if (lastname != null) {
            sb.append("\"").append(escapeJson(lastname)).append("\"");
        } else {
            sb.append("null");
        }

        // type
        sb.append(",\"type\":");
        if (type != null) {
            sb.append("\"").append(type.name()).append("\"");
        } else {
            sb.append("null");
        }

        // sum
        sb.append(",\"sum\":").append(sum != null ? sum : "null");

        sb.append("}");
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}