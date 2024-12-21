package Javaproject.Feellog.repository;

import Javaproject.Feellog.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Diary findByDate(LocalDate date);

    //List<Diary> findByUserId(Long userId);

    // userId와 date로 Diary 조회
    //Optional<Diary> findByUser_IdAndDate(Long userId, LocalDate date);

    List<Diary> findByUserIdAndDate(Long userId, LocalDate date);

    // 또는
    @Query("SELECT COUNT(d) FROM Diary d WHERE d.user.userId = :userId")
    long countByUserUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM diary d WHERE d.user_id = :userId ORDER BY d.date DESC LIMIT 1", nativeQuery = true)
    Optional<Diary> findLatestDiaryByUserId(@Param("userId") String userId);
    Optional<Diary> findTopByUser_UserIdOrderByDateDesc(String userId);
    // Optional로 반환하고 싶다면
    Optional<Diary> findTopByUser_IdOrderByDateDesc(Long userId);

    boolean existsByUserIdAndDate(Long userId, LocalDate date);

}
