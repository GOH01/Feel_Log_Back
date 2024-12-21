package Javaproject.Feellog.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class EmotionTopDateDTO {
    private LocalDate date; // 일기 날짜
    private Double highestPercentage; // 가장 높은 비율
}
