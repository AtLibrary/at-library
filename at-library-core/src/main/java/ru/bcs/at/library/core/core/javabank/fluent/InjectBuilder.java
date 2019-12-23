package ru.bcs.at.library.core.core.javabank.fluent;

import ru.bcs.at.library.core.core.javabank.http.responses.Inject;

public class InjectBuilder extends ResponseTypeBuilder {
    private String function = "";

    public InjectBuilder(ResponseBuilder responseBuilder) {
        super(responseBuilder);
    }

    public InjectBuilder function(String function) {
        this.function = function;
        return this;
    }

    @Override
    protected Inject build() {
        return new Inject().withFunction(function);
    }
}
