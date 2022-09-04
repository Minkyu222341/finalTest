package sparta.seed.scraping;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class HomePageTest {
  private static HomePage home;

  @BeforeAll
  public static void setUp(){
    home = new HomePage();
  }

  @AfterAll
  public static void tearDown(){
    home.closeWindow();
  }

  @Test
  public void HomePage_EnterEmail() throws InterruptedException {
    home.goToHomePage();
    Thread.sleep(2000);
    home.enterEmailText("abc@gmail.com");
    Thread.sleep(2000);
    home.enterPassword("abc");
    Thread.sleep(2000);
    home.clickLoginButton();
    Thread.sleep(2000);
    String currentURL = home.currentPageUrl();
    Assert.assertEquals("asdf", currentURL);
  }
}