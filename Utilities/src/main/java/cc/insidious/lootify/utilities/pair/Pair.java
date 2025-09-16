package cc.insidious.lootify.utilities.pair;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class Pair<K,V> {

    private final K key;
    private final V value;

    public static <K,V> Pair<K,V> from(K key, V value) {
        return new Pair<>(key, value);
    }
}