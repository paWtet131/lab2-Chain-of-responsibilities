package com.example.lab2;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class PositiveHandler extends Handler {

    public PositiveHandler(Handler processor) {
        super(processor);
    }

    public boolean process(Integer request) {
        if (request != ActionChain.SUCCESS) return super.process(request); // не свой запрос
        else { // свой
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Вы выиграли!");
            alert.setHeaderText("В мешочке оказались монетки. Забрать или сыграть на выигранное?");

            ButtonType replay = new ButtonType("Играть дальше", ButtonBar.ButtonData.YES);
            ButtonType take = new ButtonType("Забрать выигрыш", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(replay, take);

            Optional<ButtonType> option = alert.showAndWait();
            if (option.isPresent() && option.get().getButtonData() == ButtonBar.ButtonData.YES)
                return true;
            else
                return false;
        }
    }
}
