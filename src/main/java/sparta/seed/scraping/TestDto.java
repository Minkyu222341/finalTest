package sparta.seed.scraping;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TestDto {
  private String content;


  @Override
  public String toString() {
    return "TestDto{" +
            "content='" + content + '\'' +
            '}';
  }

  @Builder
  public TestDto(String imgUrl, String content) {
    this.content = content;
  }
}
