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
    // 감정별로 가장 높은 비율을 가진 날짜 검색
    @Query("SELECT ep.diary.date, ep.per " +
            "FROM EmotionPer ep " +
            "WHERE ep.emotion.emotionType = :emotionType AND ep.diary.user.id = :userId " +
            "AND ep.per = (SELECT MAX(subEp.per) " +
            "FROM EmotionPer subEp " +
            "WHERE subEp.diary.id = ep.diary.id) " +
            "ORDER BY ep.per DESC")
    List<Object[]> findTopDatesByEmotionAndUser(String emotionType, Long userId);


}
