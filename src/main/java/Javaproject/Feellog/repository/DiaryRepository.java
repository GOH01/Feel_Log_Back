package Javaproject.Feellog.repository;

import Javaproject.Feellog.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Diary findByDate(LocalDate date);

    List<Diary> findByUserId(Long userId);

    List<Diary> findByUserIdAndDate(Long userId, LocalDate date);

    long countByUser_UserId(String userId);
    // 또는
    @Query("SELECT COUNT(d) FROM Diary d WHERE d.user.userId = :userId")
    long countByUserUserId(@Param("userId") String userId);

    @Query("SELECT d FROM Diary d WHERE d.user.userId = :userId ORDER BY d.date DESC")
    List<Diary> findLatestDiaryByUserId(@Param("userId") String userId);
}
