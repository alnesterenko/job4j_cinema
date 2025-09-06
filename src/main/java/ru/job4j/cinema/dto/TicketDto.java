package ru.job4j.cinema.dto;

import java.time.LocalDateTime;

public class TicketDto {

    private int id;

    private int rowNumber;

    private int placeNumber;

    private String userName;

    private String filmName;

    private String hallName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int price;

    @SuppressWarnings("checkstyle:ParameterNumber")
    public TicketDto(int id, int rowNumber, int placeNumber, String userName, String filmName, String hallName, LocalDateTime startTime, LocalDateTime endTime, int price) {
        this.id = id;
        this.rowNumber = rowNumber;
        this.placeNumber = placeNumber;
        this.userName = userName;
        this.filmName = filmName;
        this.hallName = hallName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    @SuppressWarnings("checkstyle:ParameterNumber")
    public TicketDto(int rowNumber, int placeNumber, String userName, String filmName, String hallName, LocalDateTime startTime, LocalDateTime endTime, int price) {
        this.rowNumber = rowNumber;
        this.placeNumber = placeNumber;
        this.userName = userName;
        this.filmName = filmName;
        this.hallName = hallName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public TicketDto() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(int placeNumber) {
        this.placeNumber = placeNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFilmName() {
        return filmName;
    }

    public void setFilmName(String filmName) {
        this.filmName = filmName;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
