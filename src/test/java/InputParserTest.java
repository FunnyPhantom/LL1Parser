import org.junit.jupiter.api.Test;

import java.io.IOException;

public class InputParserTest {
    @Test
    public void canCreateInstance() {
        try {
            InputParser ip = new InputParser("");
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }


}
