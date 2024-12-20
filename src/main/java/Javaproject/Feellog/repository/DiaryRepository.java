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

    long countByUserId(Long userId);

    // 가장 최근 작성된 일기 가져오기
    @Query("SELECT d.date FROM Diary d WHERE d.user.id = :userId ORDER BY d.date DESC")
    List<LocalDate> findLatestDiaryDateByUserId(@Param("userId") Long userId);

    // Optional로 반환하고 싶다면
    Optional<Diary> findTopByUser_IdOrderByDateDesc(Long userId);


}
