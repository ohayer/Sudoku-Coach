package org.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class SudokuApiClient {

    private static final String ENDPOINT = "https://sudoku-api.vercel.app/api/dosuku";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public SudokuApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Pobiera nową planszę sudoku z API.
     * @return obiekt Grid zawierający value, solution i difficulty
     * @throws IOException jeśli błąd sieci lub parsowania
     * @throws InterruptedException jeśli żądanie zostało przerwane
     */
    public Optional<Grid> fetchNewBoard() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Nieoczekiwany kod HTTP: " + response.statusCode());
        }

        ApiResponse apiResponse = objectMapper.readValue(response.body(), ApiResponse.class);
        NewBoard board = apiResponse.getNewboard();
        if (board.getResults() < 1 || board.getGrids().isEmpty()) {
            return Optional.empty();
        }

        // zwracamy pierwszy grid (zwykle jest tylko jeden)
        return Optional.of(board.getGrids().get(0));
    }
}
