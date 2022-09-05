//package sparta.seed.crawling;
//
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class CrawlingExample {
//  private WebDriver driver;
//
//  private static final String url = "https://www.greenpeace.org/korea/?s=";
//
//  public void process() {
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
////
////    driver.close();	//탭 닫기
////    driver.quit();	//브라우저 닫기
//  }
//
//
//  /**
//   * data가져오기
//   */
//  private List<String> getDataList() throws InterruptedException {
//    List<String> list = new ArrayList<>();
//
//    driver.get(url);    //브라우저에서 url로 이동한다.
//    Thread.sleep(1000); //브라우저 로딩될때까지 잠시 기다린다.
//
//    //WebElement sentence = driver.findElement(By.cssSelector("#sentence-example-list .sentence-list li"));
//    //System.out.println(sentence.getText());
//    // findElement (끝에 s없음) 는 해당되는 선택자의 첫번째 요소만 가져온다
//    for (int i = 0; i <49; i++) {
//      Thread.sleep(100);
//      driver.findElement(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.has-load-more > button")).click();
//    }
//
//    Thread.sleep(15000);
//    List<WebElement> contents = driver.findElements(By.cssSelector("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.results-list > a"));
//    int count = 1;
//    for (WebElement content : contents) {
//      System.out.println("----------------------------------------------------------");
//      System.out.println(count + " : " + content.getText());
////      String text = content.findElement(By.tagName("div")).findElement(By.tagName("p")).getText();
//      String attribute = content.findElement(By.tagName("div")).getAttribute("style");
//      String replace = attribute.replace("-150x150", "");
//      String[] split = replace.split("\"");
//      System.out.println(split[1]);
//
//      count++;
//    }
//
//
//    return list;
//  }
//
//  public static void main(String[] args) throws InterruptedException {
//    CrawlingExample crawlingExample = new CrawlingExample();
//    crawlingExample.process();
//  }
//}
