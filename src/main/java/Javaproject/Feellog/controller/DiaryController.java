package Javaproject.Feellog.controller;

import Javaproject.Feellog.DTO.DiaryContentRequest;
import Javaproject.Feellog.DTO.DiarySummaryResponse;
import Javaproject.Feellog.domain.Diary;
import Javaproject.Feellog.domain.User;
import Javaproject.Feellog.service.DiaryService;
import Javaproject.Feellog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<DiarySummaryResponse> saveDiary(
            @RequestHeader("Authorization") String token,
            @RequestBody DiaryContentRequest request
    ) {
        // "Bearer " 제거
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();

        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰에서 userId 추출
        Diary savedDiary = diaryService.saveDiary(userId, request.getContent());

        // 저장된 Diary를 DiarySummaryResponse로 변환하여 반환
        DiarySummaryResponse response = new DiarySummaryResponse(savedDiary);
        return ResponseEntity.ok(response);
    }

    // 일기 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<DiarySummaryResponse> updateDiary(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody DiaryContentRequest request
    ) {
        // "Bearer " 제거
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰 검증
        Diary updatedDiary = diaryService.updateDiary(userId, id, request.getContent());

        // 수정된 Diary를 DiarySummaryResponse로 변환하여 반환
        DiarySummaryResponse response = new DiarySummaryResponse(updatedDiary);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<List<DiarySummaryResponse>> getDiariesByDate(
            @RequestHeader("Authorization") String token,
            @RequestParam String date
    ) {
        // "Bearer " 제거
        String tokenWithoutBearer = token.replace("Bearer ", "").trim();
        String userId = userService.tokenToUser(tokenWithoutBearer).getUserId(); // 토큰 검증

        // 특정 날짜와 userId로 일기 검색
        List<Diary> diaries = diaryService.getDiariesByUserAndDate(userId, date);

        // 응답 객체로 변환
        List<DiarySummaryResponse> response = diaries.stream()
                .map(DiarySummaryResponse::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/count")
    public ResponseEntity<Long> getUserDiariesCount(@RequestHeader("Authorization") String token) {
        try {
            // Bearer 토큰 처리
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();

            // 디버깅을 위한 로그
            System.out.println("받은 토큰: " + tokenWithoutBearer);

            User user = userService.tokenToUser(tokenWithoutBearer);
            System.out.println("찾은 사용자 ID: " + user.getUserId());

            long diaryCount = diaryService.getUserDiaryCount(user.getUserId());
            System.out.println("조회된 일기 수: " + diaryCount);

            return ResponseEntity.ok(diaryCount);
        } catch (Exception e) {
            // 상세한 에러 로깅
            System.err.println("일기 수 조회 중 에러 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(-1L); // 에러 시 -1 반환
        }
    }


}
