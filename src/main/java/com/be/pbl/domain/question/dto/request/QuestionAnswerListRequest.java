package com.be.pbl.domain.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 5개 답을 한 번에 받는 껍데기
@Getter
@NoArgsConstructor
@Schema(example = """
{
  "answers": [
    { "questionId": 1, "score": 0 },
    { "questionId": 2, "score": 3 },
    { "questionId": 3, "score": 4 },
    { "questionId": 4, "score": 2 },
    { "questionId": 5, "score": 1 }
  ]
}
""")

public class QuestionAnswerListRequest {

    private List<QuestionAnswerRequest> answers;
}
