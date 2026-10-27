package com.exemplo.blackclover.data;

/** Os 7 atributos principais. 'key' é o nome usado no comando /bc ponto. */
public enum Attr {
    STRENGTH("forca"),
    AGILITY("agilidade"),
    VITALITY("vitalidade"),
    MANA_RESERVE("reserva"),
    MANA_CONTROL("controle"),
    MAGIC("magica"),
    LUCK("sorte");

    public final String key;

    Attr(String key) { this.key = key; }

    public static Attr byKey(String key) {
        for (Attr a : values()) {
            if (a.key.equalsIgnoreCase(key)) return a;
        }
        return null;
    }
}
