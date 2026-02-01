package com.example.lab2;

import java.util.Random;

public class ActionChain {

    Handler chain;

    public static int SUCCESS = 1;
    public static int CHANCE = 2;
    public static int LOSS = 3;
    public static int HOPE = 4;

    Random generate;
    final int NUMHANDLER = 4;
    final int NUMMAX = 10;

    private int round = 0;
    private int lastType = LOSS;

    public ActionChain() {
        generate = new Random();
        buildChain();
    }

    private void buildChain() {
        // порядок обработчиков важен: по цепочке ищется тот, кто «возьмёт» запрос
        chain = new NegativeHandler(
                new ChanceHandler(
                        new HopeHandler(
                                new PositiveHandler(null)
                        )
                )
        );
    }

    // Розыгрыш (стратегия «удержать игрока»)
    public boolean process() {
        int type = generateTypeIndex(); // 0..NUMHANDLER-1
        return process(type);
    }

    public boolean process(Integer a) {
        int type = 1 + a % NUMHANDLER; // 1..NUMHANDLER, чтобы не было «пустого» типа 0
        lastType = type;
        round++;
        return chain.process(type);
    }

    public int getLastType() {
        return lastType;
    }

    private int generateTypeIndex() {
        // 0 - SUCCESS, 1 - CHANCE, 2 - LOSS, 3 - HOPE
        int r = generate.nextInt(NUMMAX); // 0..9

        // первые раунды — чаще «обнадёживаем» и даём шанс
        if (round < 3) {
            if (r < 3) return 0;   // 30% успех
            if (r < 5) return 1;   // 20% шанс
            if (r < 8) return 3;   // 30% надежда
            return 2;              // 20% проигрыш
        }

        // дальше — удержание за счёт шанса/надежды, но проигрыши тоже бывают
        if (r < 2) return 0;       // 20% успех
        if (r < 4) return 1;       // 20% шанс
        if (r < 6) return 3;       // 20% надежда
        return 2;                  // 40% проигрыш
    }
}
