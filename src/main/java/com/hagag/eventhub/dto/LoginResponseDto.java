package com.hagag.eventhub.dto;

public record LoginResponseDto(String message, UserDto user, String jwtToken) {
}
