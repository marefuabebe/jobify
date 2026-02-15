package com.webapp.jobportal.dto;

import java.io.Serializable;
import java.util.Date;

public class RealtimeChatDTO implements Serializable {
    private Integer id;
    private String message;
    private Date timestamp;
    private Boolean isRead;
    private Boolean isEdited;
    private Boolean isDeleted;
    private ChatUserDTO senderId;
    private ChatUserDTO receiverId;
    private Integer jobId;
    private String attachmentPath;
    private String attachmentType;

    public RealtimeChatDTO() {
    }

    public RealtimeChatDTO(Integer id, String message, Date timestamp, Boolean isRead,
            ChatUserDTO senderId, ChatUserDTO receiverId, Integer jobId) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.jobId = jobId;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsEdited() {
        return isEdited != null ? isEdited : false;
    }

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }

    public Boolean getIsDeleted() {
        return isDeleted != null ? isDeleted : false;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public ChatUserDTO getSenderId() {
        return senderId;
    }

    public void setSenderId(ChatUserDTO senderId) {
        this.senderId = senderId;
    }

    public ChatUserDTO getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(ChatUserDTO receiverId) {
        this.receiverId = receiverId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public static class ChatUserDTO implements Serializable {
        private int userId;
        private String email;

        public ChatUserDTO() {
        }

        public ChatUserDTO(int userId, String email) {
            this.userId = userId;
            this.email = email;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
