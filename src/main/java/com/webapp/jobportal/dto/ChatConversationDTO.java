package com.webapp.jobportal.dto;

import com.webapp.jobportal.entity.Users;

import java.util.Date;

public class ChatConversationDTO {
    private Users chatPartner;
    private String lastMessage;
    private Date lastMessageTimestamp;
    private int unreadCount;
    private String formattedTime;
    private String photosImagePath;

    public ChatConversationDTO(Users chatPartner, String lastMessage, Date lastMessageTimestamp, int unreadCount) {
        this.chatPartner = chatPartner;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.unreadCount = unreadCount;
    }

    public Users getChatPartner() {
        return chatPartner;
    }

    public void setChatPartner(Users chatPartner) {
        this.chatPartner = chatPartner;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Date lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public void setFormattedTime(String formattedTime) {
        this.formattedTime = formattedTime;
    }

    public String getPhotosImagePath() {
        return photosImagePath;
    }

    public void setPhotosImagePath(String photosImagePath) {
        this.photosImagePath = photosImagePath;
    }
}
