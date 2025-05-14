package ua.hudyma.Theater2025.model;

import java.io.Serializable;

public record SeatRequest(int row, int seat) implements Serializable {}
