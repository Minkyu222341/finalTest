package sparta.seed.scraping;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class SeleniumConfig {

  static {
    System.setProperty("webdriver.chrome.driver", "C:\\chromedriver_win32\\chromedriver.exe");
  }

  private WebDriver driver;
  private static SeleniumConfig instance = null;

  private SeleniumConfig() {
    driver = new ChromeDriver();
    driver.manage().window().fullscreen();
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
  }

  // Singleton: same instance injected/autowired wherever called from
  public static SeleniumConfig getInstance() {
    if (instance != null) return instance;
    return new SeleniumConfig();
  }

  public void clickElement(WebElement element){
    element.click();
  }

  public void close(){
    driver.close();
  }

  public void navigateTo(String url){
    driver.navigate().to(url);
  }

  public WebDriver getDriver(){
    return driver;
  }

}
