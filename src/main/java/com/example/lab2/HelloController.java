package com.example.lab2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label statusLabel;

    @FXML
    private Canvas canvas;

    private Player player1;
    ActionChain action = null; // по методичке

    private Image bagImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        player1 = new Player("Игрок", 0);

        bagImage = tryLoadImage("diamond.jpg");

        drawCoinScreen();
        updateStatus();

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (action == null) return; // если цепочка отсутствует

            int bag = hitTestBag(event.getX(), event.getY());
            if (bag < 0) return; // клик не по мешочку

            boolean cont = action.process(); // обработка одинакова для всех «мешочков»

            // начисление/компенсация параллельно process() (п.7 методички)
            applyReward(action.getLastType());
            updateStatus();

            if (cont) {
                if (!init()) return; // продолжить играть и проверить монетку
                drawBagsScreen();
            } else {
                action = null; // завершить игру
                drawCoinScreen();
                updateStatus();
            }
        });
    }

    @FXML
    public void onPay(ActionEvent actionEvent) {
        player1.addNumber(1);
        updateStatus();
        if (action == null) drawCoinScreen();
    }

    @FXML
    public void onStart(ActionEvent actionEvent) {
        if (!init()) return; // проверка ликвидности
        drawBagsScreen();    // загрузка автомата
        action = new ActionChain(); // запуск механизма розыгрыша
        updateStatus();
    }

    // проверка игрока на наличие средств (по методичке)
    public boolean init() {
        if (!player1.pay(1)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Средств на счете недостаточно, еще монетку плисс!");
            alert.show();

            action = null;
            drawCoinScreen();
            updateStatus();
            return false;
        } else return true;
    }

    private void applyReward(int type) {
        if (type == ActionChain.SUCCESS) {
            player1.addNumber(2);
        } else if (type == ActionChain.CHANCE) {
            // «бесплатно сыграть ещё раз» — возвращаем монетку
            player1.addNumber(1);
        }
    }

    private void updateStatus() {
        String game = (action == null) ? "Игра: остановлена" : "Игра: идет";
        statusLabel.setText("Игрок: " + player1.getName() + " | Баланс: " + player1.getNumber() + " | Сыграно: " + player1.getCount() + " | " + game);
    }

    private GraphicsContext g() {
        return canvas.getGraphicsContext2D();
    }

    private void clearScreen() {
        GraphicsContext gc = g();
        gc.setFill(Color.web("#111827"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawCoinScreen() {
        clearScreen();
        GraphicsContext gc = g();

        gc.setFill(Color.web("#E5E7EB"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 20));
        gc.fillText("Вставьте монетку и нажмите «Старт»", 24, 48);

        double cx = canvas.getWidth() / 2.0;
        double cy = canvas.getHeight() / 2.0 + 10;
        double r = Math.min(canvas.getWidth(), canvas.getHeight()) * 0.16;

        gc.setFill(Color.web("#FBBF24")); // жёлтая «монетка»
        gc.fillOval(cx - r, cy - r, r * 2, r * 2);
        gc.setStroke(Color.web("#92400E"));
        gc.setLineWidth(4);
        gc.strokeOval(cx - r, cy - r, r * 2, r * 2);

        gc.setFill(Color.web("#111827"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 22));
        gc.fillText("₽", cx - 7, cy + 8);
    }

    private void drawBagsScreen() {
        clearScreen();
        GraphicsContext gc = g();

        gc.setFill(Color.web("#E5E7EB"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 18));
        gc.fillText("Выберите один из 4 мешочков", 24, 42);

        for (int i = 0; i < 4; i++) {
            BagRect r = bagRect(i);

            gc.setFill(Color.web("#1F2937"));
            gc.fillRoundRect(r.x, r.y, r.w, r.h, 18, 18);
            gc.setStroke(Color.web("#6B7280"));
            gc.setLineWidth(2);
            gc.strokeRoundRect(r.x, r.y, r.w, r.h, 18, 18);

            // номер мешочка
            gc.setFill(Color.web("#E5E7EB"));
            gc.setFont(Font.font("System", FontWeight.BOLD, 16));
            gc.fillText(String.valueOf(i + 1), r.x + 10, r.y + 22);

            // картинка diamond.jpg (если есть), иначе простая заглушка
            if (bagImage != null) {
                drawCenteredImage(gc, bagImage, r.x + 18, r.y + 32, r.w - 36, r.h - 50);
            } else {
                gc.setFill(Color.web("#9CA3AF"));
                double px = r.x + r.w / 2.0;
                double py = r.y + r.h / 2.0 + 10;
                gc.fillPolygon(
                        new double[]{px, px + 34, px, px - 34},
                        new double[]{py - 34, py, py + 34, py},
                        4
                );
            }
        }
    }

    private Image tryLoadImage(String name) {
        try (InputStream in = HelloApplication.class.getResourceAsStream(name)) {
            if (in == null) return null;
            return new Image(in);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void drawCenteredImage(GraphicsContext gc, Image img, double x, double y, double w, double h) {
        double iw = img.getWidth();
        double ih = img.getHeight();
        if (iw <= 0 || ih <= 0) return;

        double scale = Math.min(w / iw, h / ih);
        double dw = iw * scale;
        double dh = ih * scale;
        double dx = x + (w - dw) / 2.0;
        double dy = y + (h - dh) / 2.0;
        gc.drawImage(img, dx, dy, dw, dh);
    }

    private int hitTestBag(double x, double y) {
        for (int i = 0; i < 4; i++) {
            BagRect r = bagRect(i);
            if (x >= r.x && x <= r.x + r.w && y >= r.y && y <= r.y + r.h) return i;
        }
        return -1;
    }

    private BagRect bagRect(int index) {
        double pad = 24;
        double gap = 18;
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        double gridTop = 60;
        double gridH = h - gridTop - pad;
        double gridW = w - 2 * pad;

        double cellW = (gridW - gap) / 2.0;
        double cellH = (gridH - gap) / 2.0;

        int row = index / 2;
        int col = index % 2;

        double x = pad + col * (cellW + gap);
        double y = gridTop + row * (cellH + gap);
        return new BagRect(x, y, cellW, cellH);
    }

    private static class BagRect {
        final double x, y, w, h;

        private BagRect(double x, double y, double w, double h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}