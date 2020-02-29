package ru.appavlov.at.library.core.core.javabank.http.imposters;

import org.json.simple.JSONObject;
import ru.appavlov.at.library.core.core.javabank.http.core.Stub;

import java.util.HashMap;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class Imposter extends HashMap {
    private static final String PORT = "port";
    private static final String PROTOCOL = "protocol";
    private static final String STUBS = "stubs";
    private static final String REQUESTS = "requests";

    public Imposter() {
        this.put(PROTOCOL, "http");
        this.put(STUBS, newArrayList());
        this.put(REQUESTS, newArrayList());
    }

    public static Imposter fromJSON(JSONObject json) {
        Imposter imposter = new Imposter();
        imposter.putAll(json);

        return imposter;
    }

    public static Imposter anImposter() {
        return new Imposter();
    }

    public Imposter onPort(int port) {
        this.put(PORT, port);
        return this;
    }

    public Imposter addStub(Stub stub) {
        getStubs().add(stub);
        return this;
    }

    public Imposter withStub(Stub stub) {
        this.remove(STUBS);
        this.put(STUBS, newArrayList());
        addStub(stub);
        return this;
    }

    public List<Stub> getStubs() {
        return ((List) get(STUBS));
    }

    public Stub getStub(int index) {
        return getStubs().get(index);
    }

    public List<String> getRequests() {
        List requests = newArrayList();
        for (JSONObject json : (List<JSONObject>) get(REQUESTS)) {
            requests.add(json.toJSONString());
        }
        return requests;
    }

    public String getRequest(int index) {
        if (getRequests().size() == 0) {
            return null;
        }
        return getRequests().get(index - 1);
    }

    public String getLastRequest() {
        if (getRequests().size() == 0) {
            return null;
        }
        return getRequests().get(getRequests().size() - 1);
    }

    public JSONObject toJSON() {
        return new JSONObject();
    }

    public String toString() {
        return toJSON().toJSONString();
    }

    public int getPort() {
        return Integer.valueOf(String.valueOf(get(PORT)));
    }
}
