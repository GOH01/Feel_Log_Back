package Javaproject.Feellog.repository;

import Javaproject.Feellog.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Diary findByDate( LocalDate date);
}
