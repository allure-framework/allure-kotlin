package io.qameta.allure.kotlin.testdata;

public class DummyCard {

    private final String number;

    public DummyCard(final String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "DummyCard{" +
                "number='" + number + '\'' +
                '}';
    }
}
