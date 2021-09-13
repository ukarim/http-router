package com.ukarim.httprouter;

public class RouterMatch<T> {

    private final Params params;

    private final T attachment;

    public RouterMatch(Params params, T attachment) {
        this.params = params;
        this.attachment = attachment;
    }

    public Params getParams() {
        return params;
    }

    public T getAttachment() {
        return attachment;
    }
}
