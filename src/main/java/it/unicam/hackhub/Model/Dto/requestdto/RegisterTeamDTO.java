package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.PayoutMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterTeamDTO {

    private String contactEmail;
    private PayoutMethod payoutMethod;
    private String payoutRef;

}