package org.example.hint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.*;

public class HintManager {

    private final Map<Integer, List<Hint>> hintsMap = new HashMap<>();

    public HintManager(String resourceJson) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceJson)) {
            if (is == null) throw new RuntimeException("Nie znaleziono pliku: " + resourceJson);
            ObjectMapper mapper = new ObjectMapper();
            List<Hint> all = mapper.readValue(is, new TypeReference<List<Hint>>(){});
            // grupujemy i tasujemy
            all.forEach(h -> hintsMap.computeIfAbsent(h.getNumber(), k -> new ArrayList<>()).add(h));
            hintsMap.values().forEach(Collections::shuffle);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się wczytać wskazówek z " + resourceJson, e);
        }
    }

    /** Zwraca i usuwa losową podpowiedź dla danej liczby */
    public Optional<Hint> getRandomHint(int number) {
        List<Hint> list = hintsMap.get(number);
        if (list == null || list.isEmpty()) return Optional.empty();
        return Optional.of(list.remove(0));
    }
}
