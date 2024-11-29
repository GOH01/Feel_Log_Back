package Javaproject.Feellog.DTO;

import Javaproject.Feellog.domain.Diary;
import lombok.Getter;

@Getter
public class DiarySummaryResponse {
    private String userName;
    private String content;
    private String date;

    public DiarySummaryResponse(Diary diary) {
        this.userName = diary.getUser().getUserName();
        this.content = diary.getContent();
        this.date = diary.getDate().toString();
    }
}
