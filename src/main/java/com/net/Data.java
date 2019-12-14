package com.net;

import java.util.ArrayList;
import java.util.List;

public class Data {
    private List<Sample> data = new ArrayList<>();
    private List<Sample> normData = new ArrayList<>();
    private Limits limits;

    public List<Sample> getData() {
        return data;
    }

    public void setData(List<Sample> data) {
        this.data = data;
    }

    public List<Sample> getNormData() {
        return normData;
    }

    public void setNormData(List<Sample> normData) {
        this.normData = normData;
    }

    public Limits getLimits() {
        return limits;
    }

    public void setLimits(Limits limits) {
        this.limits = limits;
    }
}
