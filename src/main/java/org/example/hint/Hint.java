package org.example.hint;

public class Hint {
    private String title;
    private String text;
    private String type;
    private int number;

    // konstruktor dla Jacksona
    public Hint() {}

    public String getTitle() { return title; }
    public String getText()  { return text; }
    public String getType()  { return type; }
    public int    getNumber(){ return number; }

    @Override
    public String toString() {
        return title + " (" + type + ")";
    }
}
