package name.lx;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       try{

//           LocalTime localTime = LocalTime.parse("23:15");
//           System.out.println(localTime);
//
//           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//           Date d = sdf.parse("2018-01-01 24:45:12");
//           System.out.println(sdf.format(d));


           System.out.println(String.format("%s  ---  %s", "08:10", parseShowTime("08:10")));
           System.out.println(String.format("%s  ---  %s", "00:10", parseShowTime("00:10")));
           System.out.println(String.format("%s  ---  %s", "24:10", parseShowTime("24:10")));
           System.out.println(String.format("%s  ---  %s", "25:10", parseShowTime("25:10")));
       }catch (Exception e){
           throw new RuntimeException(e);
       } finally {
           System.out.println("----3");
       }
    }

    private static String parseShowTime(String showTime) {
        Integer hour = Integer.parseInt(showTime.substring(0, 2));
        if (hour >= 24){
            hour = hour - 24;
        }
        String hourStr = String.valueOf(hour);
        if (hourStr.length() == 1){
            hourStr = "0" + hourStr;
        }

        return hourStr + showTime.substring(2);
    }
}
