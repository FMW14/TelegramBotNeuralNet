package com.net;

import java.util.ArrayList;
import java.util.List;

public class Sample {
    private List<Double> enter = new ArrayList<>();
    private List<Double> exit = new ArrayList<>();

    public Sample(List<Double> enter, List<Double> exit) {
        this.enter = enter;
        this.exit = exit;
    }

    public Sample(List<Double> enter) {
        this.enter = enter;
    }

    //    public List<Double> normalizeEnter(double minx, double maxx){
//        List<Double> enterN = new ArrayList<>();
//
//        for (int i = 0; i < enter.size(); i++) {
//            enterN.add(
//                    (enter.get(i) - minx)/(maxx - minx)
//            );
//        }
//
//        return enterN;
//    }

//    public List<Double> normalizeExit(double miny, double maxy){
//        List<Double> exitN = new ArrayList<>();
//
//        for (int i = 0; i < exit.size(); i++) {
//            exitN.add(
//                    (exit.get(i) - miny)/(maxy - miny)
//            );
//        }
//
//        return exitN;
//    }
//
//    public List<Double> denormalizeExit(List<Double> exitN, double miny, double maxy){
//        List<Double> exitDN = new ArrayList<>();
//
//        for (int i = 0; i < exitN.size(); i++) {
//            exitN.add(
//                    (miny + exitN.get(i))/(maxy - miny)
//            );
//        }
//
//        return exitDN;
//    }


    public List<Double> getEnter() {
        return enter;
    }

    public void setEnter(List<Double> enter) {
        this.enter = enter;
    }

    public List<Double> getExit() {
        return exit;
    }

    public void setExit(List<Double> exit) {
        this.exit = exit;
    }
}
