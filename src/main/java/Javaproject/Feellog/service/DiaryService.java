package Javaproject.Feellog.service;

import Javaproject.Feellog.domain.Diary;
import Javaproject.Feellog.domain.User;
import Javaproject.Feellog.exception.IdNotFoundException;
import Javaproject.Feellog.repository.DiaryRepository;
import Javaproject.Feellog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private  final UserService userService;


    // 일기 저장
    @Transactional
    public Diary saveDiary(String userId, String content) {
        User user = userRepository.findByUserId(userId);
        LocalDate today = LocalDate.now();

        // 사용자가 오늘 날짜에 작성한 일기가 있는지 확인
        boolean exists = diaryRepository.existsByUserIdAndDate(user.getId(), today);
        if (exists) {
            throw new RuntimeException("해당 날짜에 이미 작성한 일기가 있습니다. 날짜: " + today);
        }

        Diary diary = new Diary(user, today, content);
        return diaryRepository.save(diary);
    }


    // 특정 날짜의 일기 수정
    @Transactional
    public Diary updateDiary(String content, String token, LocalDate date) {
        User user = userService.tokenToUser(token);
        Long userId = user.getId();

        List<Diary> diaries = diaryRepository.findByUserIdAndDate(userId, date);

        if (diaries.isEmpty()) {
            throw new RuntimeException("해당 날짜의 일기를 찾을 수 없습니다. 날짜: " + date);
        }

        Diary diary = diaries.get(0);
        diary.updateDiary(content);

        return diaryRepository.save(diary);
    }





    // 특정 날짜의 일기 삭제
    @Transactional
    public void deleteDiary(String token, LocalDate date) {
        User user = userService.tokenToUser(token); // 토큰을 이용해 유저 가져오기
        Long userId = user.getId();

        List<Diary> diaries = diaryRepository.findByUserIdAndDate(userId, date); // 해당 날짜의 일기 조회
        if (diaries.isEmpty()) {
            throw new RuntimeException("해당 날짜의 일기를 찾을 수 없습니다.");
        }

        Diary diary = diaries.get(0); // 해당 날짜의 첫 번째 일기 선택
        diaryRepository.delete(diary); // 일기 삭제
    }



    // 특정 유저의 특정 날짜 일기 조회
    public List<Diary> getDiariesByUserAndDate(String userId, String date) {
        // userId를 사용해 User 엔티티 조회
        User user = userRepository.findByUserId(userId);

        // 문자열로 받은 날짜를 LocalDate로 변환
        LocalDate parsedDate = LocalDate.parse(date);

        // 해당 유저와 날짜에 해당하는 일기 검색
        return diaryRepository.findByUserIdAndDate(user.getId(), parsedDate);
    }
    public long getUserDiaryCount(String userId) {
        // User를 조회한 뒤, DiaryRepository에서 일기 개수 가져오기
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return diaryRepository.countByUserUserId(user.getUserId());
    }

    public LocalDate getLatestDiary(String token){
        User user = userService.tokenToUser(token);
        if(user==null){
            throw new IdNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return diaryRepository.findTopByUser_UserIdOrderByDateDesc(user.getUserId())
                .map(Diary::getDate)
                .orElseThrow(() -> new NoSuchElementException("사용자의 일기가 존재하지 않습니다."));
    }
}

