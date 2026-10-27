package com.hagag.eventhub.dto;

public record TodoDto(Long userId, Long id, String title, boolean completed) {
}
