package Javaproject.Feellog.domain;

import Javaproject.Feellog.repository.EmotionRepository;
import Javaproject.Feellog.repository.RecommendedActivityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecommendedActivityInitializer implements CommandLineRunner {

    private final EmotionRepository emotionRepository;
    private final RecommendedActivityRepository recommendedActivityRepository;

    public RecommendedActivityInitializer(EmotionRepository emotionRepository, RecommendedActivityRepository recommendedActivityRepository) {
        this.emotionRepository = emotionRepository;
        this.recommendedActivityRepository = recommendedActivityRepository;
    }

    @Override
    public void run(String... args) {
        if (recommendedActivityRepository.count() == 0) {
            List<Emotion> emotions = emotionRepository.findAll();
            for (Emotion emotion : emotions) {
                switch (emotion.getEmotionType()) {
                    case "joy" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "공원에서 산책하기", "자연 속에서 여유로운 시간을 보내보세요."),
                                    new RecommendedActivity(emotion, "친구에게 전화하기", "친구와 즐겁게 대화하며 즐거운 시간을 나누세요."),
                                    new RecommendedActivity(emotion, "코미디 영화 보기", "웃음을 주는 코미디 영화를 즐겨보세요."),
                                    new RecommendedActivity(emotion, "취미활동 즐기기", "즐겨하는 취미활동에 몰입해보세요."),
                                    new RecommendedActivity(emotion, "음악 감상하기", "기분 좋은 음악을 들어보세요."),
                                    new RecommendedActivity(emotion, "야외 운동하기", "자연 속에서 가벼운 운동을 즐겨보세요."),
                                    new RecommendedActivity(emotion, "책 읽기", "흥미로운 책을 읽으며 마음의 안정을 찾아보세요."),
                                    new RecommendedActivity(emotion, "새로운 카페 방문하기", "색다른 분위기의 카페를 찾아보세요."),
                                    new RecommendedActivity(emotion, "요리해보기", "새로운 요리를 시도해보세요.")
                            ));
                    case "anger" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "깊게 숨쉬기 연습하기", "심호흡으로 마음을 차분히 가라앉혀보세요."),
                                    new RecommendedActivity(emotion, "일기 쓰기", "감정을 글로 표현하며 스트레스를 풀어보세요."),
                                    new RecommendedActivity(emotion, "잔잔한 음악 듣기", "마음을 안정시키는 음악을 들어보세요."),
                                    new RecommendedActivity(emotion, "명상하기", "명상으로 마음을 차분히 다스려보세요."),
                                    new RecommendedActivity(emotion, "산책하기", "혼자만의 시간을 가지며 걷기를 해보세요."),
                                    new RecommendedActivity(emotion, "운동하기", "스트레스를 해소하기 위해 땀을 흘려보세요."),
                                    new RecommendedActivity(emotion, "미술 치료하기", "감정을 그림으로 표현해보세요."),
                                    new RecommendedActivity(emotion, "차 한잔 마시기", "따뜻한 차를 마시며 마음을 진정시키세요."),
                                    new RecommendedActivity(emotion, "신뢰하는 사람과 대화하기", "신뢰하는 사람에게 마음을 털어놓아보세요.")
                            ));
                    case "fear" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "믿을 수 있는 사람과 대화하기", "믿을 수 있는 사람에게 고민을 털어놓으세요."),
                                    new RecommendedActivity(emotion, "동기부여 되는 책 읽기", "긍정적인 에너지를 주는 책을 읽어보세요."),
                                    new RecommendedActivity(emotion, "이완 운동 해보기", "긴장을 풀어주는 이완 운동을 시도해보세요."),
                                    new RecommendedActivity(emotion, "심호흡 연습하기", "긴장을 완화하는 심호흡을 연습하세요."),
                                    new RecommendedActivity(emotion, "명상하기", "명상으로 마음의 평화를 찾아보세요."),
                                    new RecommendedActivity(emotion, "안전한 장소 찾기", "안전하고 편안한 공간에 머물러보세요."),
                                    new RecommendedActivity(emotion, "즐거운 활동 하기", "기분 전환을 위해 좋아하는 활동에 참여하세요."),
                                    new RecommendedActivity(emotion, "걷기 운동", "걷기를 통해 마음을 정리해보세요."),
                                    new RecommendedActivity(emotion, "긍정적인 말 반복하기", "스스로에게 긍정적인 말을 반복해보세요.")
                            ));
                    case "sadness" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "기분 좋은 영화 보기", "감정을 달래주는 따뜻한 영화를 감상하세요."),
                                    new RecommendedActivity(emotion, "사랑하는 사람들과 시간 보내기", "주변 사람들과 함께하며 마음을 치유하세요."),
                                    new RecommendedActivity(emotion, "창의적인 활동 해보기", "창의적인 활동으로 새로운 에너지를 느껴보세요."),
                                    new RecommendedActivity(emotion, "아침 햇살 받기", "아침 햇살을 받으며 긍정적인 에너지를 느껴보세요."),
                                    new RecommendedActivity(emotion, "간단한 운동하기", "가벼운 스트레칭이나 요가로 몸을 풀어보세요."),
                                    new RecommendedActivity(emotion, "감동적인 영화 보기", "감동을 줄 수 있는 영화를 감상하세요."),
                                    new RecommendedActivity(emotion, "꽃 구경하기", "꽃이 피어 있는 곳을 찾아 자연과 함께하세요."),
                                    new RecommendedActivity(emotion, "손글씨 연습", "손글씨를 연습하며 마음을 가다듬어 보세요."),
                                    new RecommendedActivity(emotion, "차 한 잔 하기", "따뜻한 차 한 잔으로 마음의 평화를 찾아보세요.")
                            ));
                    case "love" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "진심 어린 편지 쓰기", "소중한 사람에게 진심 어린 마음을 전달하세요."),
                                    new RecommendedActivity(emotion, "누군가를 위해 요리하기", "정성을 담아 맛있는 요리를 만들어보세요."),
                                    new RecommendedActivity(emotion, "함께 의미 있는 시간 보내기", "소중한 사람들과 특별한 시간을 보내세요."),
                                    new RecommendedActivity(emotion, "로맨틱한 음악 감상하기", "로맨틱한 음악을 들으며 사랑을 느껴보세요."),
                                    new RecommendedActivity(emotion, "사랑의 시 읽기", "사랑을 주제로 한 시를 감상해보세요."),
                                    new RecommendedActivity(emotion, "손편지 보내기", "소중한 사람에게 직접 손으로 쓴 편지를 보내세요."),
                                    new RecommendedActivity(emotion, "사진 앨범 만들기", "함께 찍은 사진으로 앨범을 만들어보세요."),
                                    new RecommendedActivity(emotion, "작은 선물 준비하기", "사랑을 표현할 수 있는 작은 선물을 준비하세요."),
                                    new RecommendedActivity(emotion, "캔들 라이트 디너 준비하기", "로맨틱한 저녁을 위해 특별한 디너를 준비하세요.")
                            ));
                    case "surprise" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "새로운 취미 시작하기", "새로운 취미로 일상을 새롭게 만들어보세요."),
                                    new RecommendedActivity(emotion, "새로운 장소 탐험하기", "익숙하지 않은 곳에서 새로운 경험을 쌓아보세요."),
                                    new RecommendedActivity(emotion, "요리 실험하기", "새로운 요리를 시도하며 즐거움을 느껴보세요."),
                                    new RecommendedActivity(emotion, "전혀 다른 스타일 시도하기", "평소 시도하지 않았던 스타일로 변화를 줘보세요."),
                                    new RecommendedActivity(emotion, "스카이 다이빙", "모험적인 활동으로 놀라움을 체험해보세요."),
                                    new RecommendedActivity(emotion, "서프라이즈 파티 기획", "소중한 사람을 위한 서프라이즈 파티를 준비하세요."),
                                    new RecommendedActivity(emotion, "특별한 이벤트 참여", "특별한 이벤트나 전시회를 방문해보세요."),
                                    new RecommendedActivity(emotion, "미스터리 게임 참여", "미스터리 테마의 게임이나 방탈출을 시도해보세요."),
                                    new RecommendedActivity(emotion, "새로운 기술 배우기", "새로운 기술을 배워보며 자신을 도전해보세요.")
                            ));
                }
            }
        }
    }
}
