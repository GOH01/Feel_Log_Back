package Javaproject.Feellog.repository;

import Javaproject.Feellog.domain.EmotionPer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionPerRepository extends JpaRepository<EmotionPer, Long> {

}
