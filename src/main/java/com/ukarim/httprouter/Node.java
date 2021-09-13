package com.ukarim.httprouter;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

    private final String name;

    private final boolean isParam;

    private final List<Node<T>> childNodes = new ArrayList<>();

    private T attachment;

    public Node(String name, boolean isParam) {
        this.name = name;
        this.isParam = isParam;
    }

    public String getName() {
        return name;
    }

    public boolean isParam() {
        return isParam;
    }

    public List<Node<T>> getChildNodes() {
        return childNodes;
    }

    public T getAttachment() {
        return attachment;
    }

    public void setAttachment(T attachment) {
        this.attachment = attachment;
    }
}
