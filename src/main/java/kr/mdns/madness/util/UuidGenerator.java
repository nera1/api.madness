package kr.mdns.madness.util;

import java.util.UUID;
import org.springframework.stereotype.Component;
import com.github.f4b6a3.uuid.UuidCreator;

@Component
public class UuidGenerator {

    public UUID generateV7() {
        return UuidCreator.getTimeOrdered();
    }

    public String generateV7AsString() {
        return generateV7().toString();
    }
}