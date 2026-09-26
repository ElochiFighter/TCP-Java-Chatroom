import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;

public record Message(String name, String payload, Instant instant) implements Serializable {
    public Message(String name, String payload) {
        this(name, payload, Instant.now());
    }

    @Override
    public String toString() {
        return "%tD %s -%s".formatted(instant.atZone(ZoneId.systemDefault()), payload, name);
    }
}