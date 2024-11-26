package Javaproject.Feellog.controller;

import Javaproject.Feellog.service.EmotionPerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

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
}
