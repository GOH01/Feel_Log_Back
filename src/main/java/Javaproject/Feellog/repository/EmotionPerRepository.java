package Javaproject.Feellog.repository;

import Javaproject.Feellog.domain.EmotionPer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmotionPerRepository extends JpaRepository<EmotionPer, Long> {
    @Query("SELECT e FROM EmotionPer e WHERE MONTH(e.diary.date) = :month AND YEAR(e.diary.date) = :year")
    List<EmotionPer> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
    List<EmotionPer> findByDiaryId(Long diaryId);
}
