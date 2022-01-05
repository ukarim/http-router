package com.ukarim.httprouter;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

public final class Params {

    private static final String[] EMPTY_ARR = new String[0];

    private Map<String, String[]> params;

    public void addParam(String name, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        var paramsArr = params.get(name);
        if (paramsArr == null) {
            paramsArr = new String[]{ value };
            params.put(name, paramsArr);
        } else {
            // grow and copy
            paramsArr = Arrays.copyOf(paramsArr, paramsArr.length + 1);
            paramsArr[paramsArr.length - 1] = value;
        }
    }

    public String[] getParams(String name) {
        if (params == null) {
            return EMPTY_ARR;
        }
        return params.getOrDefault(name, EMPTY_ARR);
    }

    public String getParam(String name) {
        if (params == null) {
            return null;
        }
        String[] params = this.params.get(name);
        if (params == null || params.length == 0) {
            return null;
        }
        return params[0];
    }
}
