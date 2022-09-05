package sparta.seed.crawling;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;
import sparta.seed.domain.Campaign;
import sparta.seed.repository.CampaignRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CrawlingV1 {
  private WebDriver driver;

  private final CampaignRepository campaignRepository;

  private static final String url = "https://www.greenpeace.org/korea/?s=";


  public void process() throws InterruptedException {
    System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");


    //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)

    driver = new ChromeDriver();
    //브라우저 선택


    try {
      getDataList();
      Thread.sleep(200000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    driver.close();	//탭 닫기
    driver.quit();	//브라우저 닫기
  }


  /**
   * data가져오기
   */
  private List<String> getDataList() throws InterruptedException {
    List<String> list = new ArrayList<>();

    driver.get(url);    //브라우저에서 url로 이동한다.
    Thread.sleep(5000); //브라우저 로딩될때까지 잠시 기다린다.

    for (int i = 0; i < 50; i++) {
      Thread.sleep(800);
      driver.findElement(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.has-load-more > button")).click();
      Thread.sleep(800);
    }


    Thread.sleep(10000);
    List<WebElement> contents = driver.findElements(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.results-list > a"));
    for (WebElement content : contents) {
      System.out.println("----------------------------------------------------------");
      String text = content.findElement(By.className("meta-box")).getText(); // 카테고리
      String pTag = content.findElement(By.tagName("P")).getText(); // 제목
      String attribute = content.findElement(By.tagName("div")).getAttribute("style");
      String replace = attribute.replace("-150x150", "");
      String[] imageUrl = replace.split("\"");  // 이미지url
      System.out.println("이미지URL : " + imageUrl[1]);
      System.out.println("카테고리 : " + text);
      System.out.println("내용 : " + pTag);

      Campaign campaign = Campaign.builder()
              .thumbnail(imageUrl[1])
              .title(pTag)
              .build();
      campaignRepository.save(campaign);
    }


    return list;
  }
}
