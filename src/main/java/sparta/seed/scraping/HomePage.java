package sparta.seed.scraping;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage {

  @FindBy(className = "gLFyf gsfi")
  private WebElement searchTextBox;

  @FindBy(className = "gNO89b")
  private WebElement searchButton;

  public HomePage(){}

  public HomePage(WebElement searchTextBox, WebElement searchButton) {
    this.searchTextBox = searchTextBox;
    this.searchButton = searchButton;
  }

  public void goToHomePage(){
    config.navigateTo(URL);
  }

  public void enterEmailText(String address){
    WebElement email = config.getDriver().findElement(By.id("email"));
    email.sendKeys(address);
  }

  public void enterPassword(String pw){
    WebElement password = config.getDriver().findElement(By.name("passwd"));
    password.sendKeys(pw);
  }

  public void clickLoginButton(){
    WebElement login = config.getDriver().findElement(By.id("SubmitLogin"));
    login.click();
  }
}