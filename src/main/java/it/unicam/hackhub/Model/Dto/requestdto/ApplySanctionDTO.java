package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.ReportResolution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplySanctionDTO {
    private ReportResolution sanctionType;
    private String reason;
    private int points;
}