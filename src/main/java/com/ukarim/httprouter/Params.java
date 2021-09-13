package com.ukarim.httprouter;

import java.util.*;

public final class Params {

    private Map<String, List<String>> params;

    public void addParam(String name, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        var paramsList = params.get(name);
        if (paramsList == null) {
            paramsList = new ArrayList<String>(1);
            params.put(name, paramsList);
        }
        paramsList.add(value);
    }

    public List<String> getParams(String name) {
        if (params == null) {
            return Collections.emptyList();
        }
        return params.getOrDefault(name, Collections.emptyList());
    }

    public String getParam(String name) {
        if (params == null) {
            return null;
        }
        List<String> params = this.params.get(name);
        if (params == null || params.isEmpty()) {
            return null;
        }
        return params.get(0);
    }
}
