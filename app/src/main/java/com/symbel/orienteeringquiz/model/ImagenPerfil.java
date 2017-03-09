package com.symbel.orienteeringquiz.model;


public class ImagenPerfil {
    private String nombreUsuario, club, url;
    private int puntuacion;

    public ImagenPerfil() {
        this.nombreUsuario = "";
        this.url = "";
        this.puntuacion = 0;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
