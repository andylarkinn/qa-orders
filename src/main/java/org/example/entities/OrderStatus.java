package org.example.entities;

import org.example.constants.ResponseEnum;

public record OrderStatus(ResponseEnum responseEnum, Double total) {
}
