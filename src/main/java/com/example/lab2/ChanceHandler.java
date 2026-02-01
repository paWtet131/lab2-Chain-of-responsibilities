package com.example.lab2;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ChanceHandler extends Handler {

    public ChanceHandler(Handler processor) {
        super(processor);
    }

    public boolean process(Integer request) {
        if (request != ActionChain.CHANCE) return super.process(request); // не свой запрос
        else { // свой
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Шанс!");
            alert.setHeaderText("Вы проиграли! Но судьба дает Вам шанс сыграть еще раз бесплатно!");

            ButtonType replay = new ButtonType("Сыграть бесплатно", ButtonBar.ButtonData.YES);
            ButtonType leave = new ButtonType("Отдохнуть", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(replay, leave);

            Optional<ButtonType> option = alert.showAndWait();
            if (option.isPresent() && option.get().getButtonData() == ButtonBar.ButtonData.YES)
                return true;
            else
                return false;
        }
    }
}
