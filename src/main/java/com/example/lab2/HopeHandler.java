package com.example.lab2;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class HopeHandler extends Handler {

    public HopeHandler(Handler processor) {
        super(processor);
    }

    public boolean process(Integer request) {
        if (request != ActionChain.HOPE) return super.process(request); // не свой запрос
        else { // свой
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Надежда");
            alert.setHeaderText("Почти повезло! Еще одна попытка — и точно выиграешь.");
            alert.getButtonTypes().setAll(new ButtonType("Продолжить"));

            alert.showAndWait();
            return true; // стратегия «удержать»
        }
    }
}
