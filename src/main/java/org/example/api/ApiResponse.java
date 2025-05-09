package org.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {
    @JsonProperty("newboard")
    private NewBoard newboard;

    public NewBoard getNewboard() {
        return newboard;
    }
}
