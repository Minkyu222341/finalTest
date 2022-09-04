package sparta.seed.scraping;

public class BasePage {

  SeleniumConfig config = SeleniumConfig.getInstance();
  protected static final String URL = "https://www.greenpeace.org/korea/?s=";

  // Example of shared code
  public void closeWindow() {
    config.close();
  }

  public String currentPageUrl(){
    return config.getDriver().getCurrentUrl();
  }
}
