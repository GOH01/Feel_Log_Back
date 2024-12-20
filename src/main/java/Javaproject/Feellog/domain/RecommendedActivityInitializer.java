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
                                    new RecommendedActivity(emotion, "친구에게 전화하기", "친구와 즐겁게 대화하며 스트레스를 해소하세요."),
                                    new RecommendedActivity(emotion, "코미디 영화 보기", "웃음을 주는 코미디 영화를 즐겨보세요.")
                            ));
                    case "anger" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "깊게 숨쉬기 연습하기", "심호흡으로 마음을 차분히 가라앉혀보세요."),
                                    new RecommendedActivity(emotion, "일기 쓰기", "감정을 글로 표현하며 스트레스를 풀어보세요."),
                                    new RecommendedActivity(emotion, "잔잔한 음악 듣기", "마음을 안정시키는 음악을 들어보세요.")
                            ));
                    case "fear" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "믿을 수 있는 사람과 대화하기", "믿을 수 있는 사람에게 고민을 털어놓으세요."),
                                    new RecommendedActivity(emotion, "동기부여 되는 책 읽기", "긍정적인 에너지를 주는 책을 읽어보세요."),
                                    new RecommendedActivity(emotion, "이완 운동 해보기", "긴장을 풀어주는 이완 운동을 시도해보세요.")
                            ));
                    case "sadness" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "기분 좋은 영화 보기", "감정을 달래주는 따뜻한 영화를 감상하세요."),
                                    new RecommendedActivity(emotion, "사랑하는 사람들과 시간 보내기", "주변 사람들과 함께하며 마음을 치유하세요."),
                                    new RecommendedActivity(emotion, "창의적인 활동 해보기", "창의적인 활동으로 새로운 에너지를 느껴보세요.")
                            ));
                    case "love" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "진심 어린 편지 쓰기", "소중한 사람에게 진심 어린 마음을 전달하세요."),
                                    new RecommendedActivity(emotion, "누군가를 위해 요리하기", "정성을 담아 맛있는 요리를 만들어보세요."),
                                    new RecommendedActivity(emotion, "함께 의미 있는 시간 보내기", "소중한 사람들과 특별한 시간을 보내세요.")
                            ));
                    case "surprise" -> recommendedActivityRepository.saveAll(
                            List.of(
                                    new RecommendedActivity(emotion, "새로운 취미 시작하기", "새로운 취미로 일상을 새롭게 만들어보세요."),
                                    new RecommendedActivity(emotion, "새로운 장소 탐험하기", "익숙하지 않은 곳에서 새로운 경험을 쌓아보세요."),
                                    new RecommendedActivity(emotion, "요리 실험하기", "새로운 요리를 시도하며 즐거움을 느껴보세요.")
                            ));
                }
            }
        }
    }
}
