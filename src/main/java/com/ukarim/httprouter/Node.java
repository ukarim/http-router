package com.ukarim.httprouter;

import java.util.ArrayList;
import java.util.List;

final class Node<T> {

    private final String name;

    private final boolean isParam;

    private final List<Node<T>> childNodes = new ArrayList<>();

    private T attachment;

    Node(String name, boolean isParam) {
        this.name = name;
        this.isParam = isParam;
    }

    String getName() {
        return name;
    }

    boolean isParam() {
        return isParam;
    }

    List<Node<T>> getChildNodes() {
        return childNodes;
    }

    T getAttachment() {
        return attachment;
    }

    void setAttachment(T attachment) {
        this.attachment = attachment;
    }
}
