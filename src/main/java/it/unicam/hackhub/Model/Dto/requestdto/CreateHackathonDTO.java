package it.unicam.hackhub.model.dto.requestdto;

import it.unicam.hackhub.model.enums.RankingPolicy;
import it.unicam.hackhub.model.valueobjs.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateHackathonDTO {
    private String name;
    private String type;
    private String regulation;
    private boolean isOnline;
    private String location;
    private String delivery;
    private double prize;
    private int maxTeamSize;
    private Period subscriptionDates;
    private Period dates;
    private String judgeEmail;
    private List<String> mentorEmails;
    private RankingPolicy rankingPolicy;
}