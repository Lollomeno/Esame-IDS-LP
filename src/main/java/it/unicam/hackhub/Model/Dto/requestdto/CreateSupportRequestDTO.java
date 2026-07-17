package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.Urgency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateSupportRequestDTO {
    private String title;
    private String description;
    private Urgency urgency;
}