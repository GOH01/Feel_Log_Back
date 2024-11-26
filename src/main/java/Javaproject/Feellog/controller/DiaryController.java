package Javaproject.Feellog.controller;

import Javaproject.Feellog.DTO.DiaryContentRequest;
import Javaproject.Feellog.domain.Diary;
import Javaproject.Feellog.service.DiaryService;
import Javaproject.Feellog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor

public class DiaryController {
    private final DiaryService diaryService;
    private final UserService userService;

    // 일기 저장
    @PostMapping("/save")
    public ResponseEntity<Diary> saveDiary(
            @RequestHeader("Authorization") String token,
            @RequestBody DiaryContentRequest request
    ) {
        // "Bearer " 제거
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();

        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰에서 userId 추출
        Diary savedDiary = diaryService.saveDiary(userId, request.getContent());
        return ResponseEntity.ok(savedDiary);
    }

    // 일기 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<Diary> updateDiary(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody DiaryContentRequest request
    ) {
        // "Bearer " 제거
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰 검증
        Diary updatedDiary = diaryService.updateDiary(userId, id, request.getContent());
        return ResponseEntity.ok(updatedDiary);
    }

    // 일기 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDiary(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id
    ) {
        // "Bearer " 제거
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰 검증
        diaryService.deleteDiary(userId, id);
        return ResponseEntity.ok("Diary deleted successfully.");
    }

    // 특정 날짜의 일기 조회
    @GetMapping("/user/date")
    public ResponseEntity<List<Diary>> getDiariesByDate(
            @RequestHeader("Authorization") String token,
            @RequestParam String date
    ) {
        // "Bearer " 제거
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰 검증
        List<Diary> diaries = diaryService.getDiariesByUserAndDate(userId, date);
        return ResponseEntity.ok(diaries);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Diary>> getUserDiaries(@PathVariable String userId) {
        return ResponseEntity.ok(diaryService.getUserDiaries(userId));
    }
}
