package io.qameta.allure.kotlin.testdata;

import java.util.List;

public class DummyEmail {

    private final String address;
    private final List<String> attachments;

    public DummyEmail(final String address, final List<String> attachments) {
        this.address = address;
        this.attachments = attachments;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "{address='" + address + '\''
                + ", attachments='" + attachments + '\'' + '}';
    }
}
