package com.example.fqtrnt.en;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fqtrnt on 16-5-8.
 */
public class Dict {
    private final String word;
    private final String phEn;
    private final String phAm;
    private int ordered;
    private int knowMe;
    private String display;

    private final List<Part> parts;
    private final String means;
    public Dict(String word, String ph_en, String ph_am, List<Part> parts) {
        this.word = word;
        this.phEn = ph_en;
        this.phAm = ph_am;
        this.parts = parts;
        this.display = word;
        this.means = Joiner.on(" ").join(parts);
    }
    public Dict(String word, String phEn, String phAm, String means) {
        this.word = word;
        this.phEn = phEn;
        this.phAm = phAm;
        this.parts = Lists.newArrayList();
        this.display = word;
        this.means = means;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getWord() {
        return word;
    }

    public String getPhEn() {
        return phEn;
    }

    public String getPhAm() {
        return phAm;
    }

    public List<Part> getParts() {
        return parts;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    public String getMeans() {
        return means;
    }

    public int getKnowMe() {
        return knowMe;
    }

    public void setKnowMe(int knowMe) {
        this.knowMe = knowMe;
    }
}
