package com.wcw.project.model.dto.ai.response;

import java.io.Serializable;

/**
 * $.payload
 *
 * @author wcw
 */
public class SparkResponsePayload implements Serializable {
    private static final long serialVersionUID = 8090192271782303700L;

    private SparkResponseChoices choices;

    private SparkResponseUsage usage;

    public SparkResponseChoices getChoices() {
        return choices;
    }

    public void setChoices(SparkResponseChoices choices) {
        this.choices = choices;
    }

    public SparkResponseUsage getUsage() {
        return usage;
    }

    public void setUsage(SparkResponseUsage usage) {
        this.usage = usage;
    }
}
