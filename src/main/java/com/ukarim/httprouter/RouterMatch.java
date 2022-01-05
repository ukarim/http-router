package com.ukarim.httprouter;

public class RouterMatch<T> {

    private final Params params;

    private final T handler;

    public RouterMatch(Params params, T handler) {
        this.params = params;
        this.handler = handler;
    }

    public Params getParams() {
        return params;
    }

    public T getHandler() {
        return handler;
    }
}
