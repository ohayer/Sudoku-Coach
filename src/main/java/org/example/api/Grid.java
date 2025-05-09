package org.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Grid {
    @JsonProperty("value")
    private int[][] value;

    @JsonProperty("solution")
    private int[][] solution;

    @JsonProperty("difficulty")
    private String difficulty;

    public int[][] getValue() {
        return value;
    }

    public int[][] getSolution() {
        return solution;
    }

    public void setSolution(int[][] solution) {
        this.solution = solution;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
