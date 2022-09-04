package sparta.seed.scraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Crawler {
  public static void main(String[] args) throws IOException {

    List<String> list = new ArrayList<>();
    Document doc = Jsoup.connect("https://www.greenpeace.org/korea/?s=").get();

    Elements con = doc.select("body > div.outer_block_container > section.advanced-search > div > div.multiple-search-result > div.results-list");
    Elements a = con.select("a");

    Iterator<Element> p = a.iterator();

    int i = 0;
    while (p.hasNext()) {

      i++;
      System.out.println(i + " " + p.next().text());
    }


  }
}