package com.net;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private final static String lSep = "/";
    private final static String wSep = "\\";

    private final static String parentDirName = "NeuralNet"; //родительская директория
    private final static String outputDirName = "Output"; //путь для выгрузки лога обучения сети
    private final static String uploadDirName = "Upload"; //путь для загруженных выборок
    private final static String netsDirName = "Nets"; //путь для сохраненных обученных сетей

    public static void toJSON(Perceptron perceptron) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(getNetsDir() + perceptron.getName() + ".json");
        mapper.writeValue(file, perceptron);
        System.out.println("json created!");
    }

    public static Perceptron toJavaObject(String name) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(getNetsDir() + name + ".json");
        System.out.println("json readed");
        return mapper.readValue(file, Perceptron.class);
    }

    public static void makeDir(File file){
        if(!file.exists()){
            if(file.mkdirs()){
                System.out.println("Kaтaлoг " + file.getAbsolutePath() + " ycпeшнo coздaн");
            } else {
                System.out.println("Kaтaлoг " + file.getAbsolutePath() + " создать не удалось");
            }
        } else {
            System.out.println("Kaтaлoг " + file.getAbsolutePath() + " уже существует");
        }
    }

    public static void createDirs(){
        makeDir(new File(getUploadDir()));
        makeDir(new File(getNetsDir()));
        makeDir(new File(getOutputDir()));
    }

    public static List<String> readSamples(String filename){
        List<String> readed = new ArrayList<>();
        try{
            FileInputStream fstream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null){
                readed.add(strLine);
            }

        }catch (IOException e){
            System.out.println("Error");
        }
        return readed;
    }

    public static Data parseData(List<String> readed, int x){ //x -  количество входных значений
        String delimeter = ";";
        List<Sample> parsed = new ArrayList<>();
//        Limits limits;

        for(String s : readed){
            String ps[] = s.split(delimeter);
            List<Double> enter = new ArrayList<>();
            List<Double> exit = new ArrayList<>();

            for(int i = 0; i < ps.length; i++) {
                if(i < x){
                    enter.add(Double.parseDouble(ps[i]));
                } else
                {
                    exit.add(Double.parseDouble(ps[i]));
                }
            }

            parsed.add(new Sample(enter, exit));
        }

        Limits limits = findMinMax(parsed);

        List<Sample> normData = new ArrayList<>();
        for (Sample s : parsed){    //вычисление нормализованных значений входов и выходов

            normData.add(normalizeOneSample(s, limits));
        }

        Data data = new Data();

        data.setData(parsed);
        data.setNormData(normData);
        data.setLimits(limits);

        return data;
    }

    public static Sample normalizeOneSample(Sample s, Limits limits){ //вычисление нормализованных значений для одного примера
        List<Double> normEnter = new ArrayList<>();
        List<Double> normExit = new ArrayList<>();

        for (int i = 0; i < s.getEnter().size(); i++) {
            normEnter.add(
                    (s.getEnter().get(i) - limits.getMinEn().get(i)) / (limits.getMaxEn().get(i) - limits.getMinEn().get(i))
            );
        }

        if (s.getExit().size() != 0){
            for (int i = 0; i < s.getExit().size(); i++) {
                normExit.add(
                        (s.getExit().get(i) - limits.getMinEx().get(i)) / (limits.getMaxEx().get(i) - limits.getMinEx().get(i))
                );
            }
        }

        return new Sample(normEnter, normExit);
    }

    public static Sample denormalizeOneSample(Sample s, Limits limits){
        List<Double> Enter = new ArrayList<>();
        List<Double> Exit = new ArrayList<>();

        for (int i = 0; i < s.getEnter().size(); i++) {
            Enter.add(
                    limits.getMinEn().get(i) + s.getEnter().get(i) * (limits.getMaxEn().get(i) - limits.getMinEn().get(i))
            );
        }

        for (int i = 0; i < s.getExit().size(); i++) {
            Exit.add(
                    limits.getMinEx().get(i) + s.getExit().get(i) * (limits.getMaxEx().get(i) - limits.getMinEx().get(i))
            );
        }

        return new Sample(Enter, Exit);
    }


    private static Limits findMinMax(List<Sample> parsed){
        List<Double> minEn = new ArrayList<>();
        List<Double> maxEn = new ArrayList<>();
        List<Double> minEx = new ArrayList<>();
        List<Double> maxEx = new ArrayList<>();

        for (int i = 0; i < parsed.get(0).getEnter().size(); i++) { //поиск мин и макс знач для каждого входа
            double max = parsed.get(0).getEnter().get(i);
            double min = parsed.get(0).getEnter().get(i);

            for (Sample s : parsed){
                if(s.getEnter().get(i) > max){
                    max = s.getEnter().get(i);
                }

                if(s.getEnter().get(i) < min){
                    min = s.getEnter().get(i);
                }
            }
            minEn.add(min);
            maxEn.add(max);
        }

        for (int i = 0; i < parsed.get(0).getExit().size(); i++) { //поиск мин и макс знач для каждого выхода
            double max = parsed.get(0).getExit().get(i);
            double min = parsed.get(0).getExit().get(i);

            for (Sample s : parsed){
                if(s.getExit().get(i) > max){
                    max = s.getExit().get(i);
                }

                if(s.getExit().get(i) < min){
                    min = s.getExit().get(i);
                }
            }
            minEx.add(min);
            maxEx.add(max);
        }

        return new Limits(minEn, maxEn, minEx, maxEx);
    }

    public static void uploadFile(String file_name, String file_id) throws IOException{
        URL url = new URL("https://api.telegram.org/bot"+System.getenv("bot_token")+"/getFile?file_id="+file_id);
        BufferedReader in = new BufferedReader(new InputStreamReader( url.openStream()));
        String res = in.readLine();
        JSONObject jresult = new JSONObject(res);
        JSONObject path = jresult.getJSONObject("result");
        String file_path = path.getString("file_path");
        URL downoload = new URL("https://api.telegram.org/file/bot" + System.getenv("bot_token") + "/" + file_path);
        FileOutputStream fos = new FileOutputStream(getUploadDir() + file_name);

        System.out.println("Start upload");
        ReadableByteChannel rbc = Channels.newChannel(downoload.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        System.out.println("Uploaded!");
    }

    public static void saveTeachLog(List<String> teachLog, String name) throws IOException{
        FileOutputStream fstream = new FileOutputStream(getOutputDir() + name + "_log.txt");
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fstream));

        for (String d : teachLog){
            br.write(d);
            br.write(System.lineSeparator());
        }

        br.flush();
        fstream.close();

        System.out.println("Лог обучения сети " + name + " записан");
    }

    public static String getUploadDir() {
        if(SystemUtils.IS_OS_LINUX){
            return System.getProperty("user.home") + lSep + parentDirName + lSep + uploadDirName + lSep;
        }
        if(SystemUtils.IS_OS_WINDOWS){
            return System.getProperty("user.home") + wSep + parentDirName + wSep + uploadDirName + wSep;
        } else {
            return uploadDirName; //SOBAD
        }
    }

    public static String getOutputDir() {
        if(SystemUtils.IS_OS_LINUX){
            return System.getProperty("user.home") + lSep + parentDirName + lSep + outputDirName + lSep;
        }
        if(SystemUtils.IS_OS_WINDOWS){
            return System.getProperty("user.home") + wSep + parentDirName + wSep + outputDirName  + wSep;
        } else {
            return outputDirName ; //SOBAD
        }
    }

    public static String getNetsDir() {
        if(SystemUtils.IS_OS_LINUX){
            return System.getProperty("user.home") + lSep + parentDirName + lSep + netsDirName + lSep;
        }
        if(SystemUtils.IS_OS_WINDOWS){
            return System.getProperty("user.home") + wSep + parentDirName + wSep + netsDirName + wSep;
        } else {
            return netsDirName; //SOBAD
        }
    }

}
