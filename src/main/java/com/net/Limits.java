package com.net;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Limits {

    @JsonProperty("minEn")
    private List<Double> minEn = new ArrayList<>();

    @JsonProperty("maxEn")
    private List<Double> maxEn = new ArrayList<>();

    @JsonProperty("minEx")
    private List<Double> minEx = new ArrayList<>();

    @JsonProperty("maxEx")
    private List<Double> maxEx = new ArrayList<>();

    public Limits(List<Double> minEn, List<Double> maxEn, List<Double> minEx, List<Double> maxEx) {
        this.minEn = minEn;
        this.maxEn = maxEn;
        this.minEx = minEx;
        this.maxEx = maxEx;
    }

    public Limits() {
    }

    public List<Double> getMinEn() {
        return minEn;
    }

    public void setMinEn(List<Double> minEn) {
        this.minEn = minEn;
    }

    public List<Double> getMaxEn() {
        return maxEn;
    }

    public void setMaxEn(List<Double> maxEn) {
        this.maxEn = maxEn;
    }

    public List<Double> getMinEx() {
        return minEx;
    }

    public void setMinEx(List<Double> minEx) {
        this.minEx = minEx;
    }

    public List<Double> getMaxEx() {
        return maxEx;
    }

    public void setMaxEx(List<Double> maxEx) {
        this.maxEx = maxEx;
    }

    @Override
    public String toString() {
        String response = "Ограничения по каждому входу: " + System.lineSeparator();

        int numberOfEnters = getMinEn().size();

        for (int i = 0; i < numberOfEnters; i++) {
            response += "Вход " + i + ": от " + getMinEn().get(i) + " до " + getMaxEn().get(i) + System.lineSeparator();
        }


        return response;
    }
}
