package ru.appavlov.at.library.core.core.javabank.fluent;

import ru.appavlov.at.library.core.core.javabank.http.responses.Response;

public abstract class ResponseTypeBuilder implements FluentBuilder {
    private ResponseBuilder parent;

    protected ResponseTypeBuilder(ResponseBuilder parent) {
        this.parent = parent;
    }

    @Override
    public ResponseBuilder end() {
        return parent;
    }

    abstract protected Response build();
}
