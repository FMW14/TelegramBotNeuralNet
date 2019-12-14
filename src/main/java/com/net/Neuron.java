package com.net;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.exp;
import static java.lang.Math.pow;

public class Neuron {

    @JsonProperty("w0")
    private double w0 = 0;

    @JsonProperty("weight")
    private List<Double> weights = new ArrayList<>();

    @JsonIgnore
    private double y = 0; //выходное значение

    @JsonIgnore
    private double S = 0; //состояние нейрона

    public Neuron() {
    }

    public Neuron(int a) { //а - количество входов
        double max = pow(a, -1);
        for (int i = 0; i < a; i++) {    //инициализация весовых коэф
            double x = Math.random() * max;
            this.weights.add(x);
        }

        this.w0 = Math.random() * max;
    }

    public void calculateCondition(List<Double> enter) {
        double sum = 0;

        for (int i = 0; i < enter.size(); i++) {
            sum += (enter.get(i) * this.weights.get(i));
        }

        this.S = w0 + sum;
    }

    public void actFuncHyperTan(double a){
        y = (exp(a * S) - 1)/(exp(a * S) + 1);
    }

    public void actFuncSig(double a, double T) {
        y = 1 / (exp((S - T) * (-a)) + 1);
    }

    public double getW0() {
        return w0;
    }

    public void setW0(double w0) {
        this.w0 = w0;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public void setWeights(List<Double> weights) {
        this.weights = weights;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getS() {
        return S;
    }

    public void setS(double s) {
        S = s;
    }
}
