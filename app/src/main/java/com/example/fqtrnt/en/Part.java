package com.example.fqtrnt.en;

/**
 * Created by fqtrnt on 16-5-8.
 */
public class Part {

    private final String part;

    private final String means;

    public Part(String part, String means) {
        this.part = part;
        this.means = means;
    }

    public String getMeans() {
        return means;
    }

    public String getPart() {
        return part;
    }

    @Override
    public String toString() {
        return part + " " + means;
    }
}
