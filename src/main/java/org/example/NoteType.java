package org.example; // или другой пакет, если необходимо

public enum NoteType {
    ЛИЧНАЯ("Личная"),
    РАБОЧАЯ("Рабочая"),
    ИДЕЯ("Идея"),
    ЗАДАЧА("Задача");

    private final String displayName;

    NoteType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
