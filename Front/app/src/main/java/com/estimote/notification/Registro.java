package com.estimote.notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Registro {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("beacon")
    @Expose
    private String beacon;
    @SerializedName("estado")
    @Expose
    private String estado;
    @SerializedName("hora")
    @Expose
    private String hora;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBecons() {
        return beacon;
    }

    public void setBecons(String becons) {
        this.beacon = becons;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}