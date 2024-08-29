package com.wcw.project.model.dto.ai.request;



import com.wcw.project.model.dto.ai.SparkChatParameter;

import java.io.Serializable;

/**
 * $.parameter
 *
 * @author wcw
 */
public class SparkRequestParameter implements Serializable {
    private static final long serialVersionUID = 4502096141480336425L;

    private SparkChatParameter chat;

    public SparkRequestParameter() {
    }

    public SparkRequestParameter(SparkChatParameter chat) {
        this.chat = chat;
    }

    public SparkChatParameter getChat() {
        return chat;
    }

    public void setChat(SparkChatParameter chat) {
        this.chat = chat;
    }
}
