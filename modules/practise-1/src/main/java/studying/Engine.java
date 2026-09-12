package studying;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Engine {
    @Getter
    private final int pedSize;

    public int getSize() {
        return pedSize;
    }

    @Override
    public String toString() {
        return "Engine{" + "size=" + pedSize + '}';
    }
}
