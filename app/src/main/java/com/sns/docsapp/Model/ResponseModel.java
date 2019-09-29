package com.sns.docsapp.Model;

import com.google.gson.annotations.SerializedName;

public class ResponseModel {
    @SerializedName("success")
    private Integer success;
    @SerializedName("errorMessage")
    private String errorMessage;
    @SerializedName("message")
    private SuccessModel message;

    public class SuccessModel {
        @SerializedName("chatBotName")
        private String chatBotName;
        @SerializedName("chatBotID")
        private Integer chatBotID;
        @SerializedName("message")
        private String message;
        @SerializedName("emotion")
        private String emotion;

        public String getChatBotName() {
            return chatBotName;
        }

        public void setChatBotName(String chatBotName) {
            this.chatBotName = chatBotName;
        }

        public Integer getChatBotID() {
            return chatBotID;
        }

        public void setChatBotID(Integer chatBotID) {
            this.chatBotID = chatBotID;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getEmotion() {
            return emotion;
        }

        public void setEmotion(String emotion) {
            this.emotion = emotion;
        }
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public SuccessModel getMessage() {
        return message;
    }

    public void setMessage(SuccessModel message) {
        this.message = message;
    }
}
