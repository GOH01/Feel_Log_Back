package Javaproject.Feellog.service;

import Javaproject.Feellog.domain.Diary;
import Javaproject.Feellog.domain.Emotion;
import Javaproject.Feellog.domain.EmotionPer;
import Javaproject.Feellog.domain.User;
import Javaproject.Feellog.exception.IdNotFoundException;
import Javaproject.Feellog.repository.DiaryRepository;
import Javaproject.Feellog.repository.EmotionPerRepository;
import Javaproject.Feellog.repository.EmotionRepository;
import Javaproject.Feellog.utils.JwtUtility;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional
public class EmotionPerService {

    private final EmotionPerRepository emotionPerRepository;
    private final EmotionRepository emotionRepository;
    private final DiaryRepository diaryRepository;
    private final UserService userService;
    private final DiaryService diaryService;

    @Value("${api.url}")
    private String API_URL;
    @Value("${api.key}") @Getter
    private String API_KEY;

    @Transactional
    public void updateEmotionDiary(String token,LocalDate date, String content){
        Long userId = userService.tokenToUser(token).getId();
        if(userId==null){
            throw new IdNotFoundException("사용자 찾을 수 없음");
        }

        // 1. 특정 날짜의 일기 조회
        Diary diary = diaryRepository.findByUserIdAndDate(userId, date);
        if (diary == null) throw new IllegalArgumentException("해당 날짜의 일기를 찾을 수 없습니다.");

        diary.updateDiary(content);
        diaryRepository.save(diary);
        // 2. 감정 분석 API 호출
        JsonObject response = callEmotionAnalysisAPI(diary.getContent());

        // 응답에서 'scored_labels' 배열 가져오기
        if (!response.has("scored_labels")) {
            throw new IllegalArgumentException("API 응답이 잘못되었습니다: 'scored_labels' 필드가 없습니다.");
        }

        JsonArray scoredLabelsArray = response.getAsJsonArray("scored_labels");

        // 3. 'scored_labels' 배열에서 label과 score 추출
        List<String> labels = new ArrayList<>();
        List<Double> scores = new ArrayList<>();

        for (int i = 0; i < scoredLabelsArray.size(); i++) {
            JsonObject labelScore = scoredLabelsArray.get(i).getAsJsonObject();
            labels.add(labelScore.get("label").getAsString());
            scores.add(labelScore.get("score").getAsDouble());
        }

        // 4. 감정 비율 정규화
        List<Double> normalizedScores = normalizeScores(scores);

        // 5. joy와 anger의 score 스왑
        Map<String, Double> swappedScores = swap(labels, normalizedScores);

        // 기존 감정 데이터 삭제
        emotionPerRepository.deleteByDiaryId(diary.getId());
        // 6. 결과 저장
        saveEmotionPer(diary, swappedScores);
    }


    @Transactional
    public void analyzeAndSaveEmotionForDate(String userToken, String content) {
        try {
            // 1. 사용자 정보 추출
            String userId = userService.tokenToUser(userToken).getUserId();

            // 2. 일기 저장 (saveDiary 호출)
            Diary diary = diaryService.saveDiary(userId, content); // '일기 내용'은 클라이언트에서 받아와야 함

            // 3. 감정 분석 API 호출 (예외 발생 가능)
            JsonObject emotionData = callEmotionAnalysisAPI(diary.getContent());

            if (!emotionData.has("scored_labels")) {
                throw new IllegalArgumentException("API 응답이 잘못되었습니다: 'scored_labels' 필드가 없습니다.");
            }

            JsonArray scoredLabelsArray = emotionData.getAsJsonArray("scored_labels");

            // 3. 'scored_labels' 배열에서 label과 score 추출
            List<String> labels = new ArrayList<>();
            List<Double> scores = new ArrayList<>();

            for (int i = 0; i < scoredLabelsArray.size(); i++) {
                JsonObject labelScore = scoredLabelsArray.get(i).getAsJsonObject();
                labels.add(labelScore.get("label").getAsString());
                scores.add(labelScore.get("score").getAsDouble());
            }
            // 4. 감정 비율 정규화
            List<Double> normalizedScores = normalizeScores(scores);

            // 5. joy와 anger의 score 스왑
            Map<String, Double> swappedScores = swap(labels, normalizedScores);

            // 6. 결과 저장
            saveEmotionPer(diary, swappedScores);

        } catch (Exception e) {
            // 예외 발생 시 트랜잭션 롤백
            throw new RuntimeException("감정 분석 중 오류가 발생했습니다. 저장이 취소되었습니다.", e);
        }
    }



    // JsonArray -> List<String> 변환 메서드
    private List<String> convertToStringList(JsonArray jsonArray) {
        if (jsonArray == null) {
            throw new IllegalArgumentException("JSON Array is null");
        }

        List<String> list = new ArrayList<>();
        jsonArray.forEach(element -> list.add(element.getAsString())); // 각 요소를 String으로 변환
        return list;
    }

    // JsonArray -> List<Double> 변환 메서드
    private List<Double> convertToDoubleList(JsonArray jsonArray) {
        if (jsonArray == null) {
            throw new IllegalArgumentException("JSON Array is null");
        }
        List<Double> list = new ArrayList<>();
        jsonArray.forEach(element -> list.add(element.getAsDouble())); // 각 요소를 Double로 변환
        return list;
    }

    private List<Double> normalizeScores(List<Double> scores) {
        double total = scores.stream().mapToDouble(Double::doubleValue).sum(); // 총합 계산
        if (total == 0) {
            throw new IllegalArgumentException("Scores의 총합이 0입니다. 정규화할 수 없습니다.");
        }
        List<Double> normalized = new ArrayList<>();

        for (double score : scores) {
            double normalizedValue = roundToTwoDecimalPlaces(score / total); // 정규화 및 소수점 반올림
            normalized.add(normalizedValue);
        }

        return normalized;
    }

    private JsonObject callEmotionAnalysisAPI(String diaryContent){
        try {
            // HttpClient 생성
            HttpClient client = HttpClient.newHttpClient();

            // 요청 본문 생성
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("text", diaryContent);

            // HttpRequest 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            // 요청 보내기
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // 응답 처리
                JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
                System.out.println("API Response: " + responseBody); // 디버깅
                return responseBody;
            } else {
                throw new RuntimeException("API 호출 실패: " + response.statusCode() + ", " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("감정 분석 API 호출 중 오류 발생", e);
        }
    }

    private Map<String, Double> swap(List<String> labels, List<Double> scores) {
        double loveValue = 0.0, angerValue = 0.0 , surpriseValue=0.0, fearValue=0.0;
        Map<String, Double> swappedScores = new HashMap<>();

        for (int i = 0; i < labels.size(); i++) {
            if("love".equalsIgnoreCase(labels.get(i))){
                loveValue = scores.get(i);
            }else if ("anger".equalsIgnoreCase(labels.get(i))){
                angerValue = scores.get(i);
            }else if ("fear".equalsIgnoreCase(labels.get(i))){
                fearValue = scores.get(i);
            }else if ("surprise".equalsIgnoreCase(labels.get(i))){
                surpriseValue = scores.get(i);
            }
        }

        for (int i = 0; i < labels.size(); i++) {
            String label = labels.get(i);
            double value = scores.get(i);

            if ("love".equalsIgnoreCase(label)) {
                swappedScores.put(label, roundToTwoDecimalPlaces(angerValue)); // anger 값을 joy에 저장
            } else if ("anger".equalsIgnoreCase(label)) {
                swappedScores.put(label, roundToTwoDecimalPlaces(loveValue)); // joy 값을 anger에 저장
            } else if ("fear".equalsIgnoreCase(label)) {
                swappedScores.put(label, roundToTwoDecimalPlaces(surpriseValue));
            }else if ("surprise".equalsIgnoreCase(label)) {
                swappedScores.put(label, roundToTwoDecimalPlaces(fearValue));
            }else {
                swappedScores.put(label, roundToTwoDecimalPlaces(value)); // 다른 감정은 그대로
            }
        }

        return swappedScores;
    }

    private void saveEmotionPer(Diary diary,Map<String, Double> swappedScores) {

        for (Map.Entry<String, Double> entry : swappedScores.entrySet()) {
            String label = entry.getKey();
            double value = entry.getValue();

            // Emotion 찾기
            System.out.println("Looking for emotion: " + label);
            Emotion emotion = emotionRepository.findByEmotionType(label);
            if (emotion == null) {
                throw new IllegalArgumentException("Emotion 타입이 존재하지 않습니다: " + label);
            }
            System.out.println("Found emotion: " + emotion.getEmotionType());

            // EmotionPer 생성 및 저장
            EmotionPer emotionPer = new EmotionPer();
            emotionPer.setDiary(diary);
            emotionPer.setEmotion(emotion);
            emotionPer.setPer(value);

            System.out.println("Saving EmotionPer: diaryId=" + diary.getId() +
                    ", emotionType=" + emotion.getEmotionType() +
                    ", per=" + value);

            emotionPerRepository.save(emotionPer);
            emotionPerRepository.flush();
        }
        System.out.println("All EmotionPer saved successfully for diaryId=" + diary.getId());
    }

    private  double roundToTwoDecimalPlaces(double value){
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public List<EmotionPer> getEmotionPer(Diary diary) {
        List<EmotionPer> emotions = emotionPerRepository.findByDiaryId(diary.getId());

        if (emotions == null || emotions.isEmpty()) {
            // 로그에 데이터가 없음을 기록
            System.out.println("No emotions found for diary ID: " + diary.getId());
        }

        return emotions;
    }

    public Map<String, Double> getMonthlyEmotionStatistics( String token, int year, int month) {

        Long userId=userService.tokenToUser(token).getId();
        // 1. 해당 월의 데이터 조회
        List<EmotionPer> emotionPerList = emotionPerRepository.findByMonthAndYear(month, year, userId);

        if (emotionPerList.isEmpty()) {
            throw new IllegalArgumentException("해당 월의 데이터가 존재하지 않습니다.");
        }

        // 2. 감정별 비율 합계 및 데이터 수 계산
        Map<String, List<Double>> emotionValues = new HashMap<>();
        for (EmotionPer emotionPer : emotionPerList) {
            String emotionType = emotionPer.getEmotion().getEmotionType();
            emotionValues.computeIfAbsent(emotionType, k -> new ArrayList<>()).add(emotionPer.getPer());
        }

        // 3. 평균 계산
        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : emotionValues.entrySet()) {
            String emotionType = entry.getKey();
            List<Double> values = entry.getValue();
            double average = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            averages.put(emotionType, roundToTwoDecimalPlaces(average));
        }

        return averages;
    }
}
