package Javaproject.Feellog.repository;

import Javaproject.Feellog.domain.RecommendedActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendedActivityRepository extends JpaRepository<RecommendedActivity, Long> {
    // 특정 Emotion ID에 속하는 추천 활동 리스트 조회
    List<RecommendedActivity> findByEmotion_Id(Long emotionId);
}
