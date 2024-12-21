package Javaproject.Feellog.controller;

import Javaproject.Feellog.DTO.EmotionTopDateDTO;
import Javaproject.Feellog.service.EmotionSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/emotion-search")
@RequiredArgsConstructor
public class EmotionSearchController {
    private final EmotionSearchService emotionSearchService;

    // 감정별로 가장 높은 비율을 가진 날짜를 반환
    @GetMapping("/top-dates")
    public ResponseEntity<List<EmotionTopDateDTO>> getTopDatesByEmotion(@RequestParam String emotionType) {
        List<EmotionTopDateDTO> topDates = emotionSearchService.getTopDatesByEmotion(emotionType);
        return ResponseEntity.ok(topDates);
    }
}
