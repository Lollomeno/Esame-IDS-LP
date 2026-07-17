package it.unicam.hackhub.model.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookSupportCallDTO {
    private LocalDateTime startsAt;
    private Duration duration;
    private String title;
    private String description;
}