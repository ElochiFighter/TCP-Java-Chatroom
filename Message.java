import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    public static final String DATE = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy"));
    public String message;
    public String name;

    public Message(String message, String name) {
        this.message = message;
        this.name = name;
    }

    public String getDate() {
        return DATE;
    }

    @Override
    public String toString() {
        return DATE + " " + message + " -" + name;
    }

}