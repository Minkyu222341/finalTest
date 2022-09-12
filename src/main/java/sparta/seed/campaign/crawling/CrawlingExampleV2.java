package sparta.seed.campaign.crawling;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CrawlingExampleV2 {
  private WebDriver driver;
  private static final String url = "https://www.greenpeace.org/korea/?s=";


  public void process() throws InterruptedException {
    System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");


    //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)

    driver = new ChromeDriver();
    //브라우저 선택


    try {
      getDataList();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


  /**
   * data가져오기
   */
  private List<String> getDataList() throws InterruptedException {
    List<String> list = new ArrayList<>();
    driver.get(url);    //브라우저에서 url로 이동한다.
    Thread.sleep(1000); //브라우저 로딩될때까지 잠시 기다린다.

    for (int i = 0; i < 49; i++) {
      driver.findElement(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.has-load-more > button")).sendKeys(Keys.ENTER);
      Thread.sleep(500);
    }

    Thread.sleep(1000);
    int count = 1;
    for (int i = 1; i < 300; i++) {
      try {
        WebElement elementList = driver.findElement(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.results-list > a:nth-child(" + count + ")"));
        elementList.sendKeys(Keys.ENTER);
      } catch (Throwable e) {
        try {
          WebElement elementList = driver.findElement(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.results-list > a:nth-child(" + (i + 1) + ")"));
          count +=1;
          elementList.sendKeys(Keys.ENTER);
        } catch (Throwable e2) {
          WebElement elementList = driver.findElement(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.results-list > a:nth-child(" + (i + 2) + ")"));
          count +=2;
          elementList.sendKeys(Keys.ENTER);
        }
      }

      WebElement element = driver.findElement(By.cssSelector("body > div.outer_block_container"));
      List<WebElement> img = element.findElements(By.tagName("img"));
      List<WebElement> p = element.findElements(By.tagName("p"));
      System.out.println("**********************************사진********************************************");
        for (WebElement webElement : img) {
          String attribute = webElement.getAttribute("src");
          System.out.println(attribute);
        }
      System.out.println("**********************************사진********************************************");

      System.out.println("**********************************본문********************************************");
      for (WebElement webElement : p) {
        String text = webElement.getText();
        if (text.equals("관련 뉴스")) {
          continue;
        }
        if (!text.equals("") && !text.equals("  ")) {
          System.out.println("P태그 : "+text);
        }
      }
      System.out.println(count+"**********************************본문********************************************");

      Thread.sleep(5000);
      this.driver.navigate().back();
      System.out.println("-----------------------------------------다음글-----------------------------------------------------");
      count++;
    }

    return list;
  }

  public static void main(String[] args) throws InterruptedException {
    CrawlingExampleV2 crawlingExampleV2 = new CrawlingExampleV2();
    crawlingExampleV2.process();

  }
}
