package org.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class NewBoard {
    @JsonProperty("grids")
    private List<Grid> grids;

    @JsonProperty("results")
    private int results;

    @JsonProperty("message")
    private String message;

    // gettery
    public List<Grid> getGrids() {
        return grids;
    }

    public int getResults() {
        return results;
    }

    public String getMessage() {
        return message;
    }
}
