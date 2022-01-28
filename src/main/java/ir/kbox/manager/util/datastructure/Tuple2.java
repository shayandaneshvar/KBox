package ir.kbox.manager.util.datastructure;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Tuple2<T, U> {
    private final T first;
    private final U second;

    public static <T, U> Tuple2<T, U> of(T t, U u) {
        return new Tuple2<>(t, u);
    }
}
