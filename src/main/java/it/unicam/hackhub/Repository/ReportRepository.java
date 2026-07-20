package it.unicam.hackhub.repository;

import it.unicam.hackhub.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> getByHackathonId(Long hackathonId);

    Report getByIdAndHackathonId(Long id, Long hackathonId);
}