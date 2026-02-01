package com.example.lab2;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class NegativeHandler extends Handler {

    public NegativeHandler(Handler processor) {
        super(processor);
    }

    public boolean process(Integer request) {
        if (request != ActionChain.LOSS) return super.process(request); // не свой запрос
        else { // свой
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Вы проиграли!");
            alert.setHeaderText("Монетка потеряна, но всегда можно отыграться!");

            ButtonType replay = new ButtonType("Продолжить играть", ButtonBar.ButtonData.YES);
            ButtonType vacation = new ButtonType("Отдохнуть", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(replay, vacation);

            Optional<ButtonType> option = alert.showAndWait();
            if (option.isPresent() && option.get().getButtonData() == ButtonBar.ButtonData.YES)
                return true;
            else
                return false;
        }
    }
}
