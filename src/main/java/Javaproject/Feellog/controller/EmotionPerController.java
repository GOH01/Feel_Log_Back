package Javaproject.Feellog.controller;

import Javaproject.Feellog.service.EmotionPerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmotionPerController {

    private final EmotionPerService emotionPerService;

    @PostMapping("/api/emotion/analyze")
    public String analyzeEmotionForDiary(@RequestParam String date){
        LocalDate diaryDate = LocalDate.parse(date);
        emotionPerService.analyzeAndSaveEmotionForDate(diaryDate);
        return "감정 분석 및 저장 완료: "+date;
    }

    @GetMapping("/statistics/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStatistics(@RequestParam int year, @RequestParam int month) {
        Map<String, Double> statistics = emotionPerService.getMonthlyEmotionStatistics(year, month);
        Map<String, Object> response = new HashMap<>();
        response.put("month", year + "-" + (month < 10 ? "0" + month : month));
        response.put("statistics", statistics);
        return ResponseEntity.ok(response);
    }
}
