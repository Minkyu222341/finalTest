package sparta.seed.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class DateUtil {

  private static class TIME_MAXIMUM {
    public static final int SEC = 60;
    public static final int MIN = 60;
    public static final int HOUR = 24;
    public static final int DAY = 30;
    public static final int MONTH = 12;
  }

  public long weekOfMonth(String createdTime) throws ParseException {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date date = formatter.parse(createdTime);
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return cal.get(Calendar.WEEK_OF_MONTH);
  }


  public String dateStatus(String startDate, String endDate) throws ParseException {
    String todayfm = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Date start = new Date(dateFormat.parse(startDate).getTime()); //시작날짜
    Date end = new Date(dateFormat.parse(endDate).getTime()); // 종료날짜
    Date today = new Date(dateFormat.parse(todayfm).getTime()); // 현재 시점

    int startCompare = start.compareTo(today);
    int endCompare = end.compareTo(today);

    if (startCompare > -1 && endCompare > -1) {
      return "before";
    }
    if (endCompare == -1) {
      return "end";
    }
    if (startCompare == -1 && endCompare >= 0) {
      return "ongoing";
    }
    return "check";
  }

}
