package Javaproject.Feellog.repository;

import Javaproject.Feellog.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    Diary findByDate(LocalDate date);

    List<Diary> findByUserId(Long userId);

    List<Diary> findByUserIdAndDate(Long userId, LocalDate date);

}
