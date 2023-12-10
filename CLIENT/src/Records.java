import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Records implements Serializable {
    private final String message;
    static List<String> records = new ArrayList<>();

    public Records(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Records{" +
                    "message='" + message + '\'' +
                '}';
    }
}
