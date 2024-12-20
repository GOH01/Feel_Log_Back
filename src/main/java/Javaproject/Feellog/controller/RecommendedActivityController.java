package Javaproject.Feellog.controller;

import Javaproject.Feellog.DTO.RecommendedActivityDTO;
import Javaproject.Feellog.domain.EmotionPer;
import Javaproject.Feellog.domain.RecommendedActivity;
import Javaproject.Feellog.repository.EmotionPerRepository;
import Javaproject.Feellog.repository.RecommendedActivityRepository;
import Javaproject.Feellog.service.RecommendedActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/recommended-activity")
@RequiredArgsConstructor
public class RecommendedActivityController {

    private final RecommendedActivityService recommendedActivityService;
    private final EmotionPerRepository emotionPerRepository; // 감정 데이터 조회
    private final RecommendedActivityRepository recommendedActivityRepository; // 추천 활동 조회

    /**
     * 일기별 추천 활동 반환
     * @param diaryId 일기의 ID
     * @return 추천 활동 정보
     */
    @GetMapping("/diary/{diaryId}")
    public ResponseEntity<List<Map<String, String>>> getRecommendationByDiary(@PathVariable Long diaryId) {
        // 1. 해당 일기의 감정 데이터 가져오기
        List<EmotionPer> emotionPers = emotionPerRepository.findByDiary_Id(diaryId);

        if (emotionPers.isEmpty()) {
            throw new IllegalArgumentException("해당 일기에 대한 감정 데이터가 없습니다.");
        }

        // 2. 가장 높은 감정 점수를 가진 데이터 찾기
        EmotionPer topEmotion = emotionPers.stream()
                .max(Comparator.comparingDouble(EmotionPer::getPer))
                .orElseThrow(() -> new IllegalArgumentException("감정 데이터를 처리할 수 없습니다."));

        Long emotionId = topEmotion.getEmotion().getId(); // 가장 높은 감정의 ID

        // 3. 해당 감정에 대한 추천 활동 랜덤 조회
        List<RecommendedActivity> activities = recommendedActivityRepository.findByEmotion_Id(emotionId);

        if (activities.isEmpty()) {
            throw new IllegalArgumentException("해당 감정에 대한 추천 활동이 없습니다.");
        }

        // 랜덤으로 추천 활동 선택 (3개)
        Collections.shuffle(activities); // 리스트를 랜덤하게 섞음
        List<RecommendedActivity> randomActivities = activities.stream()
                .limit(3) // 상위 3개 선택
                .toList();

        if (randomActivities.isEmpty()) {
            throw new IllegalStateException("추천 활동 데이터가 불완전합니다.");
        }

        // 4. 응답 데이터 구성
        List<Map<String, String>> responseList = new ArrayList<>();
        for (RecommendedActivity activity : randomActivities) {
            Map<String, String> response = new HashMap<>();
            response.put("해보아요!!", activity.getName());
            response.put("이렇게!!", activity.getDescription());
            responseList.add(response);
        }

        return ResponseEntity.ok(responseList);
    }


    //랜덤 추천 활동 조회
//    @GetMapping
//    public ResponseEntity<RecommendedActivityDTO.ResponseDTO> getRandomActivity(@RequestParam String emotionType) {
//        return ResponseEntity.ok(recommendedActivityService.getRandomActivity(emotionType));
//    }

    //추천 활동 생성
    @PostMapping
    public ResponseEntity<RecommendedActivityDTO.ResponseDTO> createActivity(@RequestBody RecommendedActivityDTO.CreateDTO createDTO) {
        return ResponseEntity.ok(recommendedActivityService.createActivity(createDTO));
    }

    //추천 활동 수정
    @PutMapping("/{id}")
    public ResponseEntity<RecommendedActivityDTO.ResponseDTO> updateActivity(
            @PathVariable Long id,
            @RequestBody RecommendedActivityDTO.UpdateDTO updateDTO) {
        return ResponseEntity.ok(recommendedActivityService.updateActivity(id, updateDTO));
    }

    //추천 활동 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
        recommendedActivityService.deleteActivity(id);
        return ResponseEntity.ok("추천 활동이 성공적으로 삭제되었습니다.");
    }



}
