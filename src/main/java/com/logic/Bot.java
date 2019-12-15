package com.logic;

import com.net.Data;
import com.net.Perceptron;
import com.net.Utils;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private final String token = System.getenv("bot_token");
    private final String botname = System.getenv("bot_name");
//    private Data data;
    private Perceptron perceptron;
    private Integer enters;
    private Integer exits;

    @Override
    public void onUpdateReceived(Update update) {
        long chat_id = update.getMessage().getChatId();

        if (update.hasMessage() && update.getMessage().hasText()) {
//            = update.getMessage().getChatId();
            Message msg = update.getMessage();
//            long chat_id = msg.getChatId();
            String txt = msg.getText();

            if(txt.equals("/start")){
                String text = "Введите /help для получения списка команд";
                responseText(text, chat_id);
            }

            if(txt.equals("/help")){
                responseHelp(chat_id);
            }

            if(txt.equals("/netinfo")){
                if(perceptron != null){
                    String text = "Название:" + perceptron.getName()
                                + "Кол-во входов:"
                                + "Кол-во выходов"
                                + "";

                    responseText(text, chat_id);
                } else {
                    String text = "Нейронная сеть не инициализирована. Создайте новую сеть командой /newnet";
                    responseText(text, chat_id);
                }
//                    String text = "Количество входов: " + ent;
//                    responseText(text, chat_id);
            }

            if(txt.equals("/newnet")){
                try {
                    this.perceptron = new Perceptron(enters, exits);
                    String text = "Создана новая сеть." + System.lineSeparator()
                                + "Входов: " + enters + System.lineSeparator()
                                + "Выходов: " + exits;
                    responseText(text, chat_id);
//                    exits = null;
//                    ent = null;
                } catch (Exception e){
                    responseText("Сеть не инициализирована, не хватает данных. Введите количество входов (EN) и выходов (EX)", chat_id);
                }
            }

            if(txt.equals("/teachSamples")){
                File uploadDir = new File(Utils.getUploadDir());
//                Collection<File> files = FileUtils.listFiles(uploadDir, new String[] {"java"}, true);
                String[] files = uploadDir.list(); //список файлов в директории
                if (files != null){
                    String text = "Список загруженных файлов с обучающими выборками:" + System.lineSeparator();
                    for (String filename : files) {
//                        text += file.getName() + System.lineSeparator();
                        text += filename + System.lineSeparator();
                    }
                    responseText(text, chat_id);
                } else {
                    String text;
                    text = "Нет загруженных выборок";
                    responseText(text, chat_id);
                }
            }

            if(txt.contains("/teach")){
                String[] splited = update.getMessage().getText().split(" ");

                if(splited.length > 1){
                    if(perceptron != null){
                        if (isPerceptronSettingsOk()){
                            String path = Utils.getUploadDir() + splited[splited.length-1];
                            Data data = Utils.parseData(Utils.readSamples(path), perceptron.getA());
                            List<String> teachLog = perceptron.teach(data);
                            try {
                                Utils.saveTeachLog(teachLog, perceptron.getName());
                            } catch (IOException e){
                                System.out.println(e);
                                responseText("Ошибка сохранения лога обучения", chat_id);
                            }
                            try {
                                File savedLog = new File(Utils.getOutputDir() + perceptron.getName() + ".txt");
                                String msgtext = perceptron.getName() + "_" + splited[splited.length-1];
                                sendDocUploadingAFile(chat_id, savedLog, msgtext);
                            } catch(TelegramApiException e){
                                System.out.println(e);
                                responseText("Ошибка отправки лога обучения", chat_id);
                            }
                        } else {
                            responseText("Не все параметры сети заданы. Введите /help для просмотра всех параметров", chat_id);
                        }

                    } else {
                        responseText("Нейронная сеть не инициализирована. Создайте новую сеть командой /newnet", chat_id);
                    }

                } else {
                    responseText("Введите название сети через пробел после команды", chat_id);
                }
            }

            if(txt.equals("/save")){
                try {
                    Utils.toJSON(perceptron);
                } catch (IOException e){
                    System.out.println(e);
                    responseText("Не удалось сохранить сеть " + perceptron.getName(), chat_id);
                }
                responseText("Сеть " + perceptron.getName() + " сохранена", chat_id);
            }

            if(txt.contains("/load")){
                String[] splited = update.getMessage().getText().split(" ");
                if(splited.length > 1){
                    try {
                        perceptron = Utils.toJavaObject(splited[1]);
                    } catch (IOException e){
                        System.out.println(e);
                        responseText("Ошибка загрузки сети", chat_id);
                    }
                } else {
                    responseText("Введите название сети через пробел после команды", chat_id);
                }

            }

            if(txt.contains("EN")){
                String[] splited = update.getMessage().getText().split("=");
                if(splited.length > 1){
                    Integer temp = Integer.parseInt(splited[splited.length-1]);
                    String text;
                    if(temp > 0){
                        enters = temp;
                        text = "Количество входов: " + enters;
                    } else {
                        text = "Значение не может быть меньше нуля";
                    }
                    responseText(text, chat_id);
                } else {
                    responseText("Не удалось распознать значение", chat_id);
                }
            }

            if(txt.contains("EX")){
                String[] splited = update.getMessage().getText().split("=");
                if(splited.length > 1){
                    Integer temp = Integer.parseInt(splited[splited.length-1]);
                    String text;
                    if(temp > 0){
                        exits = temp;
                        text = "Количество выходов: " + exits;
                    } else {
                        text = "Значение не может быть меньше нуля";
                    }
                    responseText(text, chat_id);
                } else {
                    responseText("Не удалось распознать значение", chat_id);
                }
            }

            if(txt.contains("ALPHA")){
                String[] splited = update.getMessage().getText().split("=");
                if(splited.length > 1){
                    String text;
                    double alpha = Double.parseDouble(splited[splited.length-1]);
                    if(perceptron != null){
                        perceptron.setAlpha(alpha);
                        text = "alpha: " + alpha;
                    } else {
                        text = "Нейронная сеть не инициализирована. Создайте новую сеть командой /newnet";
                    }
                    responseText(text, chat_id);

                } else {
                    responseText("Не удалось распознать значение", chat_id);
                }
            }

            if(txt.contains("T")){
                String[] splited = update.getMessage().getText().split("=");
                if(splited.length > 1){
                    String text;
                    double t = Double.parseDouble(splited[splited.length-1]);
                    if(perceptron != null){
                        perceptron.setT(t);
                        text = "T: " + t;
                    } else {
                        text = "Нейронная сеть не инициализирована. Создайте новую сеть командой /newnet";
                    }
                    responseText(text, chat_id);
                } else {
                    responseText("Не удалось распознать значение", chat_id);
                }
            }

            if(txt.contains("V")){
                String[] splited = update.getMessage().getText().split("=");
                if(splited.length > 1){
                    double v = Double.parseDouble(splited[splited.length-1]);
                    String text;
                    if(v > 0 && v <= 1){
                        if(perceptron != null){
                            perceptron.setV(v);
                            text = "v: " + v;
                        } else {
                            text = "Нейронная сеть не инициализирована. Создайте новую сеть командой /newnet";
                        }
                    } else {
                        text = "Значение не может быть меньше 0 и больше 1";
                    }
                    responseText(text, chat_id);

                } else {
                    responseText("Не удалось распознать значение", chat_id);
                }
            }

            if(txt.contains("ERA")){
                String[] splited = update.getMessage().getText().split("=");
                if(splited.length > 1){
                    int era = Integer.parseInt(splited[splited.length-1]);
                    String text;
                    if(era > 0){
                        if(perceptron != null){
                            perceptron.setEra(era);
                            text = "era: " + era;
                        } else {
                            text = "Нейронная сеть не инициализирована. Создайте новую сеть командой /newnet";
                        }
                    } else {
                        text = "Значение не может быть меньше нуля";
                    }
                    responseText(text, chat_id);

                } else {
                    responseText("Не удалось распознать значение", chat_id);
                }
            }

            if(txt.contains("NAME")){
                String[] splited = update.getMessage().getText().split("=");
                if(splited.length > 1){
                    String text;
                    String name = splited[splited.length-1];
                    if(perceptron != null){
                        perceptron.setName(name);
                        text = "name: " + name;
                    } else {
                        text = "Нейронная сеть не инициализирована. Создайте новую сеть командой /newnet";
                    }
                    responseText(text, chat_id);
                } else {
                    responseText("Не удалось распознать значение", chat_id);
                }
            }

            if(txt.contains("FUNC")){
                String[] splited = update.getMessage().getText().split("=");
                if(splited.length > 1){
                    String text;
                    String func = splited[splited.length-1];
                    if(!func.equals("sig") && !func.equals("tan")){
                        text = "Неверное название функции";
                    } else {
                        if(perceptron != null){
                            perceptron.setFunc(func);
                            text = "function: " + func;
                        } else {
                            text = "Нейронная сеть не инициализирована. Создайте новую сеть командой /newnet";
                        }
                    }

                    responseText(text, chat_id);
                } else {
                    responseText("Не удалось распознать значение", chat_id);
                }
            }




            // Set variables

//            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
//            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
//            List<InlineKeyboardButton> rowInline = new ArrayList<>();
//            rowInline.add(new InlineKeyboardButton().setText("Create new Net").setCallbackData("create_net"));
//            rowInline.add(new InlineKeyboardButton().setText("Load Net").setCallbackData("load_net"));
//            // Set the keyboard to the markup
//            rowsInline.add(rowInline);
//            // Add it to the message
//            markupInline.setKeyboard(rowsInline);


//            InlineKeyboardButton button1 = new InlineKeyboardButton();
//            button1.setText("Test123");
//            -------------------------
//            String message_text = update.getMessage().getText();
//            long chat_id = update.getMessage().getChatId();
//
//            SendMessage message = new SendMessage() // Create a message object object
//                    .setChatId(chat_id)
//                    .setText(message_text);
//
//            message.setReplyMarkup(markupInline);
//            try {
//                execute(message); // Sending our message object to user
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//            ----------------------------
        }
//        else if (update.hasCallbackQuery()) {
//            // Set variables
//            String call_data = update.getCallbackQuery().getData();
////            long message_id = update.getCallbackQuery().getMessage().getMessageId();
////            long chat_id = update.getCallbackQuery().getMessage().getChatId();
//
//            if (call_data.equals("create_net")) {
//                String answer = "Updated message text";
//                SendMessage message = new SendMessage() // Create a message object object
//                        .setChatId(chat_id)
//                        .setText("Net created");
//
//                try {
//                    execute(message);
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        if (update.hasMessage() && update.getMessage().hasDocument()){
            Message msg = update.getMessage();
            String text;
            if ((msg.getDocument().getFileName().contains(".csv"))){
                try {
                    Utils.uploadFile(msg.getDocument().getFileName(), msg.getDocument().getFileId());
                    text = "Файл " + msg.getDocument().getFileName() + " уcпешно загружен";
                } catch (IOException e){
                    System.out.println(e);
                    text = "Файл НЕ был загружен" + System.lineSeparator() + e;
                }
            } else {
                text = "Файл с обучающей выборкой должен иметь формат csv";
            }
            responseText(text, chat_id);
        }
    }

    private void sendDocUploadingAFile(Long chatId, java.io.File save, String caption) throws TelegramApiException {
        SendDocument sendDocumentRequest = new SendDocument();
        sendDocumentRequest.setChatId(chatId);
        sendDocumentRequest.setNewDocument(save);
        sendDocumentRequest.setCaption(caption);
        sendDocument(sendDocumentRequest);
    }

    private boolean isPerceptronSettingsOk() {
        if (this.perceptron.getName() == null ||
                this.perceptron.getEra() == null ||
                this.perceptron.getAlpha() == null ||
                this.perceptron.getT() == null ||
                this.perceptron.getFunc() == null ||
                this.perceptron.getV() == null) {
            return false;
        } else {
            return true;
        }
    }

    private void responseText(String text, long chat_id){
        SendMessage message = new SendMessage() // Create a message object object
                .setChatId(chat_id)
                .setText(text);
        try {
            execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void responseHelp(long chat_id){
        String text = "/newnet - создать новую сеть" + System.lineSeparator()
                + "/netinfo - информация о текущей сети" + System.lineSeparator()
                + "/teachSamples - список загруженных файлов с выборками" + System.lineSeparator()
                + "/teach filename - обучить текущую нейроную сеть на основек обучающей выборки filename" + System.lineSeparator()
                + "/save - сохранить текущую нейронную сеть" + System.lineSeparator()
                + "/load name - загрузить сеть с именем name" + System.lineSeparator()
                + "EN - количество входов" + System.lineSeparator()
                + "EX - количество выходов" + System.lineSeparator()
                + "NAME - название" + System.lineSeparator()
                + "ALPHA - коэф. alpha" + System.lineSeparator()
                + "T - коэф. T" + System.lineSeparator()
                + "V - скорость обучения" + System.lineSeparator()
                + "ERA - количество итераций" + System.lineSeparator()
                + "FUNC (sig/tan) - активационная функция";
        responseText(text, chat_id);
    }

    @Override
    public String getBotUsername() {
        return botname;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}