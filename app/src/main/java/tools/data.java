package tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class data {
    public Bitmap myBitmap;
    public String imageId;
    public String title;
    public String channel;
    public int type;
    long count;
    public View view;
//    public String extra;
    public String id;
    public String date, duration="";

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    boolean unlocked = false;
    public data(){
        super();
    }

    String getDateFomart(String dateparsed) {
        Date result=null;
        String format="";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            result = sdf.parse(dateparsed);
                             //"2016-03-24T17:00:01.000Z"
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LocalDate dataDate = new LocalDate(result.getTime());
        LocalDate now = LocalDate.now();
        int wks = Weeks.weeksBetween(dataDate, now).getWeeks();
        if (wks==0) {
            int days = Days.daysBetween(dataDate,now).getDays();
            if (days==0)
                format = "Today";
            else
            format = Days.daysBetween(dataDate,now).getDays()+" days ago";
        }
        else if (wks<=4) {
            format = (wks)+" weeks ago";
        } else if (wks<52) {
            format = (wks/4)+" months ago";
        } else {
            format = (wks/52)+" years ago";
        }
        return format;
    }

//    public data(String ImageId, String title, String chan, int type, Context c, String ids, String date) {
//        super();
//
//        this.title = title;
//        imageId = ImageId;
//        channel  = chan;
//        this.type = type;
//        this.ids = ids;
//        this.date=date;
//        System.out.println(this.date);
//
//        try {
//            myBitmap = Picasso.with(c).load(imageId).get();
////            System.out.println(myBitmap.getWidth());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public data(String ImageId, String title, String chan, int type, Context c, String id) {
        super();

        this.title = title;
        imageId = ImageId;
        channel  = chan;
        this.type = type;
        this.id = id;


        try {

            myBitmap = Picasso.with(c).load(imageId).get();
//            System.out.println(myBitmap.getWidth());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public data(String ImageId, String title, String chan, int type) {
        super();

        this.title = title;
        imageId = ImageId;
        channel  = chan;
        this.type = type;

    }
//    public data(String ImageId, String title, String chan, int type, String extra) {
//        super();
//        this.title = title;
//        imageId = ImageId;
//        channel  = chan;
//        this.type = type;
//        this.extra = extra;
//    }

    public data(String ImageId, String title, String channelTitle, String dateText, int type, String id) {
        super();
        this.title = title;
        this.imageId = ImageId;
        this.channel  = channelTitle;
        this.type = type;
        this.date = getDateFomart(dateText);
        this.id = id;
    }

//    public data(String ImageId, String title,  String ids) {
//        super();
//        this.ids = ids;
//        this.title = title;
//        imageId = ImageId;
//    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getMyBitmap() {
        return myBitmap;
    }

    public String getDate() {
        return date;
    }

    public String getChannel() {
        return channel;
    }

    public String getImageId() {
        return imageId;
    }

    public int getType() {
        return type;
    }

    public String getDuration() {
        return dur(duration);
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

//    public long getCount() {
//        return count;
//    }
 public String getCount() {
     if (count>0)
        return " • "+ format(count)+" Views";
     else
        return "";
    }

    public void setCount(long count) {
        this.count = count;
    }

    String dur(String data) {
        try {
            String dataCopy = data.replace("PT", "");
            int h = dataCopy.indexOf("H");
            int m = dataCopy.indexOf("M");
            int s = dataCopy.indexOf("S");
            String finalData = "";
            if (dataCopy.contains("H")&&dataCopy.contains("M") && dataCopy.contains("S")) {
                finalData = pad(dataCopy.substring(0, h)) + ":" + pad(dataCopy.substring(h + 1, m)) + ":" + pad(dataCopy.substring(m + 1, s));
            }if (dataCopy.contains("H")&&dataCopy.contains("M")) {
                finalData = pad(dataCopy.substring(0, h)) + ":" + pad(dataCopy.substring(h + 1, m));
            } else if (dataCopy.contains("M") && dataCopy.contains("S")) {
                finalData = pad(dataCopy.substring(h + 1, m)) + ":" + pad(dataCopy.substring(m + 1, s));
            } else if (dataCopy.contains("M")) {
                finalData = pad(dataCopy.substring(h + 1, m))+":00";
            } else if (dataCopy.contains("S")) {
                finalData = "00:" + pad(dataCopy.substring(m + 1, s));
            }
            return  finalData;
        }catch (StringIndexOutOfBoundsException e) {
            System.out.println(data);
            return "";
        }

    }

    String pad(String data) {
        if (data.length()>1)
            return data;
        else
            return "0"+data;
    }

    public String format(long num) {

        long temp = num / 1000000;
        if(temp > 0) {
            return temp + "M";
        }

        temp = num / 1000;
        if (temp > 0) {
            return temp + "K";
        }

        return String.valueOf(num);
    }

}