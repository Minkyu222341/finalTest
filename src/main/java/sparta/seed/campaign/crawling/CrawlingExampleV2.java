//package sparta.seed.crawling;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class CrawlingExampleV2 {
//  private WebDriver driver;
//
//
//  private static final String url = "https://www.greenpeace.org/korea/?s=";
//
//
//  public void process() throws InterruptedException {
//    System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");
//
//
//    //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)
//
//    driver = new ChromeDriver();
//    //브라우저 선택
//
//
//    try {
//      getDataList();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//  }
//
//
//  /**
//   * data가져오기
//   */
//  private List<String> getDataList() throws InterruptedException {
//    List<String> list = new ArrayList<>();
//    this.driver.get(url);    //브라우저에서 url로 이동한다.
//    Thread.sleep(1000); //브라우저 로딩될때까지 잠시 기다린다.
//
//    for (int i = 0; i < 50; i++) {
//      Thread.sleep(800);
//      this.driver.findElement(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.has-load-more > button")).click();
//    }
//
//    Thread.sleep(1000);
//  int count = 1;
//    for (int i = 1; i < 300; i++) {
//
//
//     this.driver.findElement(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.results-list > a:nth-child(" + i + ")")).click();
//
//
//      Thread.sleep(2000);
//      String text1 = this.driver.findElement(By.cssSelector("section.section-post-content.ct-container")).getText();
//      Thread.sleep(3000);
//      this.driver.navigate().back();
//      System.out.println("-----------------------------------------------------------");
//      System.out.println(count+" : "+text1);
//      count++;
//    }
//
//
//
//
//
//    return list;
//  }
//
//  public static void main(String[] args) throws InterruptedException {
//    CrawlingExampleV2 crawlingExampleV2 = new CrawlingExampleV2();
//    crawlingExampleV2.process();
//
//  }
//}
