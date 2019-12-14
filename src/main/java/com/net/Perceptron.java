package com.net;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Perceptron {

    @JsonProperty("name")
    private String name;

    @JsonProperty("a")
    private int a = 0; //количество входов

    @JsonProperty("b")
    private int b = 0; //количество выходов и нейронов

    @JsonProperty("alpha")
    private double alpha = 0;

    @JsonProperty("t")
    private double T = 0;

    @JsonProperty("v")
    private double v = 0;

    @JsonProperty("era")
    private double era = 0;

    @JsonProperty("comError")
    private double comError = 0;

    @JsonProperty("neurons")
    private List<Neuron> neurons = new ArrayList<>();

    @JsonProperty("limits")
    private Limits limits;

    @JsonProperty("func")
    private String func;

//    DecimalFormat f = new DecimalFormat("0.00000");
//        System.out.println(f.format(neuron.getY()));

    public Perceptron() {
    }

    public Perceptron(int a, int b) {
        this.a = a;
        this.b = b;

        for(int i = 0; i < b; i++){
            this.neurons.add(new Neuron(a));
        }
    }

    public void teach(Data data, double v, double alp, double t, int era){
        DecimalFormat f = new DecimalFormat("0.00000");

        limits = data.getLimits();
        alpha = alp;
        this.T = t;

        for (int i = 0; i < era; i++) {
            Collections.shuffle(data.getNormData());
            double err = 0;

            for (Sample s : data.getNormData()) {

                List<Double> deltas = new ArrayList<>();

                for (Neuron neuron : neurons) {

                    neuron.calculateCondition(s.getEnter()); //подача нормализованных значений на вход

                    if(!func.equals("sig") && !func.equals("tan")){
                        if (func.equals("sig")){ //применение активационной функции
                            neuron.actFuncSig(alp, T);
                        }
                        if (func.equals("tan")){
                            neuron.actFuncHyperTan(alp);
                        }
                    } else {
                        System.out.println("Неизвестная активационная функция");
                        break;
                    }

//                    neuron.actFuncSig(alp, T); //применение активационной функции
//                    neuron.actFuncHyperTan(alp);
                }

                for (int j = 0; j < s.getExit().size(); j++) { //рассчет погрешности
                    deltas.add(s.getExit().get(j) - neurons.get(j).getY());
                }

                err += calculateError(deltas);

                int z = 0;
                for (Neuron neuron : neurons){ //вычисление новых вес. коэф.
                    neuron.setW0(neuron.getW0() + (v * deltas.get(z)));
                    List<Double> newWeights = new ArrayList<>();

                    for (int j = 0; j < neuron.getWeights().size(); j++) {
                        newWeights.add(
                                neuron.getWeights().get(j) + (v * deltas.get(z) * s.getEnter().get(j))
                        );
                    }

                    neuron.setWeights(newWeights);
                    z++;
                }
            }

            double sizeb = data.getNormData().size() * b;
            double z = 1/sizeb;
            double err2 = Math.sqrt( z * err);
            comError = err2;
//            System.out.println("E" + i + " = " + f.format(err2));
        }

//        System.out.println("Train finished");
    }

    public Sample use(Sample sample){

        Sample normSample = Utils.normalizeOneSample(sample, limits);

        for (Neuron neuron : neurons) {
            neuron.calculateCondition(normSample.getEnter()); //подача нормализованных значений на вход
            neuron.actFuncSig(alpha, T); //применение активационной функции
            normSample.getExit().add(neuron.getY());
        }

        return Utils.denormalizeOneSample(normSample, limits);
    }

    private double calculateError(List<Double> deltas){
        double sumd = 0;
        for (double d : deltas) {
            sumd +=  Math.pow(d, 2);
        }

        return sumd;
    }

    public Limits getLimits() {
        return limits;
    }

    public void setLimits(Limits limits) {
        this.limits = limits;
    }

    public double getComError() {
        return comError;
    }

    public void setComError(double comError) {
        this.comError = comError;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getT() {
        return T;
    }

    public void setT(double t) {
        T = t;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public double getV() {
        return v;
    }

    public double getEra() {
        return era;
    }

    public void setEra(double era) {
        this.era = era;
    }

    public void setV(double v) {
        this.v = v;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(List<Neuron> neurons) {
        this.neurons = neurons;
    }
}
