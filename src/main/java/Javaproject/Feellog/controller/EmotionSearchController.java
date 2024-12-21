package Javaproject.Feellog.controller;

import Javaproject.Feellog.DTO.EmotionTopDateDTO;
import Javaproject.Feellog.service.EmotionSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotion-search")
@RequiredArgsConstructor
public class EmotionSearchController {
    private final EmotionSearchService emotionSearchService;

    // 감정별로 가장 높은 비율을 가진 날짜를 반환
    @GetMapping("/top-dates")
    public ResponseEntity<List<EmotionTopDateDTO>> getTopDatesByEmotion(
            @RequestHeader("Authorization") String token,
            @RequestParam String emotionType
    ) {
        try {
            // Emotion 데이터 조회
            List<EmotionTopDateDTO> response = emotionSearchService.getTopDatesByEmotionAndToken(
                    token.replace("Bearer ", "").trim(),
                    emotionType
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
