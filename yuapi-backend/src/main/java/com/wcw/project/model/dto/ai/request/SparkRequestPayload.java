package com.wcw.project.model.dto.ai.request;

import com.wcw.project.model.dto.ai.request.function.SparkRequestFunctions;

import java.io.Serializable;

/**
 * $.payload
 *
 * @author wcw
 */
public class SparkRequestPayload implements Serializable {
    private static final long serialVersionUID = 2084163918219863102L;

    private SparkRequestMessage message;

    private SparkRequestFunctions functions;

    public SparkRequestPayload() {
    }

    public SparkRequestPayload(SparkRequestMessage message) {
        this.message = message;
    }

    public SparkRequestPayload(SparkRequestMessage message, SparkRequestFunctions functions) {
        this.message = message;
        this.functions = functions;
    }

    public SparkRequestMessage getMessage() {
        return message;
    }

    public void setMessage(SparkRequestMessage message) {
        this.message = message;
    }

    public SparkRequestFunctions getFunctions() {
        return functions;
    }

    public void setFunctions(SparkRequestFunctions functions) {
        this.functions = functions;
    }
}
