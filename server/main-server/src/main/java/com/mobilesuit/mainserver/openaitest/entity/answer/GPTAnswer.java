package com.mobilesuit.mainserver.openaitest.entity.answer;


import com.mobilesuit.mainserver.openaitest.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GPTAnswer extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "gpt_answer", length = 1500, nullable = false)
  private String answer;

  public GPTAnswer(String answer) {
    this.answer = answer;
  }
}
