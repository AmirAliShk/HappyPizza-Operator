package ir.food.operatorAndroid.helper;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Amirreza Erfanian on 30/12/2017.
 * A.E
 * version = 2
 */

public class DateHelper {

    private class SolarCalendar {

        public String strWeekDay = "";
        public String strMonth = "";

        int date;
        int month;
        int year;

        public SolarCalendar() {
            Date MiladiDate = new Date();
            calcSolarCalendar(MiladiDate);
        }

        public SolarCalendar(Date MiladiDate) {
            calcSolarCalendar(MiladiDate);
        }

        private void calcSolarCalendar(Date MiladiDate) {

            int ld;

            int miladiYear = MiladiDate.getYear() + 1900;
            int miladiMonth = MiladiDate.getMonth() + 1;
            int miladiDate = MiladiDate.getDate();
            int WeekDay = MiladiDate.getDay();

            int[] buf1 = new int[12];
            int[] buf2 = new int[12];

            buf1[0] = 0;
            buf1[1] = 31;
            buf1[2] = 59;
            buf1[3] = 90;
            buf1[4] = 120;
            buf1[5] = 151;
            buf1[6] = 181;
            buf1[7] = 212;
            buf1[8] = 243;
            buf1[9] = 273;
            buf1[10] = 304;
            buf1[11] = 334;

            buf2[0] = 0;
            buf2[1] = 31;
            buf2[2] = 60;
            buf2[3] = 91;
            buf2[4] = 121;
            buf2[5] = 152;
            buf2[6] = 182;
            buf2[7] = 213;
            buf2[8] = 244;
            buf2[9] = 274;
            buf2[10] = 305;
            buf2[11] = 335;

            if ((miladiYear % 4) != 0) {
                date = buf1[miladiMonth - 1] + miladiDate;

                if (date > 79) {
                    date = date - 79;
                    if (date <= 186) {
                        switch (date % 31) {
                            case 0:
                                month = date / 31;
                                date = 31;
                                break;
                            default:
                                month = (date / 31) + 1;
                                date = (date % 31);
                                break;
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;

                        switch (date % 30) {
                            case 0:
                                month = (date / 30) + 6;
                                date = 30;
                                break;
                            default:
                                month = (date / 30) + 7;
                                date = (date % 30);
                                break;
                        }

                        year = miladiYear - 621;
                    }
                } else {
                    if ((miladiYear > 1996) && (miladiYear % 4) == 1) {
                        ld = 11;
                    } else {
                        ld = 10;
                    }
                    date = date + ld;

                    switch (date % 30) {
                        case 0:
                            month = (date / 30) + 9;
                            date = 30;
                            break;
                        default:
                            month = (date / 30) + 10;
                            date = (date % 30);
                            break;
                    }
                    year = miladiYear - 622;
                }
            } else {
                date = buf2[miladiMonth - 1] + miladiDate;

                if (miladiYear >= 1996) {
                    ld = 79;
                } else {
                    ld = 80;
                }
                if (date > ld) {
                    date = date - ld;

                    if (date <= 186) {
                        switch (date % 31) {
                            case 0:
                                month = (date / 31);
                                date = 31;
                                break;
                            default:
                                month = (date / 31) + 1;
                                date = (date % 31);
                                break;
                        }
                        year = miladiYear - 621;
                    } else {
                        date = date - 186;

                        switch (date % 30) {
                            case 0:
                                month = (date / 30) + 6;
                                date = 30;
                                break;
                            default:
                                month = (date / 30) + 7;
                                date = (date % 30);
                                break;
                        }
                        year = miladiYear - 621;
                    }
                } else {
                    date = date + 10;

                    switch (date % 30) {
                        case 0:
                            month = (date / 30) + 9;
                            date = 30;
                            break;
                        default:
                            month = (date / 30) + 10;
                            date = (date % 30);
                            break;
                    }
                    year = miladiYear - 622;
                }

            }

            switch (month) {
                case 1:
                    strMonth = "فروردين";
                    break;
                case 2:
                    strMonth = "ارديبهشت";
                    break;
                case 3:
                    strMonth = "خرداد";
                    break;
                case 4:
                    strMonth = "تير";
                    break;
                case 5:
                    strMonth = "مرداد";
                    break;
                case 6:
                    strMonth = "شهريور";
                    break;
                case 7:
                    strMonth = "مهر";
                    break;
                case 8:
                    strMonth = "آبان";
                    break;
                case 9:
                    strMonth = "آذر";
                    break;
                case 10:
                    strMonth = "دي";
                    break;
                case 11:
                    strMonth = "بهمن";
                    break;
                case 12:
                    strMonth = "اسفند";
                    break;
            }
            switch (WeekDay) {

                case 0:
                    strWeekDay = "يکشنبه";
                    break;
                case 1:
                    strWeekDay = "دوشنبه";
                    break;
                case 2:
                    strWeekDay = "سه شنبه";
                    break;
                case 3:
                    strWeekDay = "چهارشنبه";
                    break;
                case 4:
                    strWeekDay = "پنج شنبه";
                    break;
                case 5:
                    strWeekDay = "جمعه";
                    break;
                case 6:
                    strWeekDay = "شنبه";
                    break;
            }

        }

        public int getDate() {
            return date;
        }

        public int getMonth() {
            return month;
        }
    }

    public static YearMonthDate getCurrentJalaliDate() {
        Locale loc = new Locale("en_US");
        DateHelper util = new DateHelper();
        SolarCalendar sc = util.new SolarCalendar();
        Date d = new Date();
        return new YearMonthDate(sc.year, sc.month, sc.strMonth, sc.date, sc.strWeekDay, d.getHours(), d.getMinutes(), d.getSeconds());
    }

    public static Date getCurrentGregorianDate() {
        Date date = new Date();
        return date;
    }

    public static YearMonthDate gregorianToJalali(Date date) {
        DateHelper util = new DateHelper();
        SolarCalendar sc = util.new SolarCalendar(date);
        return new YearMonthDate(sc.year, sc.month, sc.strMonth, sc.date, sc.strWeekDay, date.getHours(), date.getMinutes(), date.getSeconds());
    }

    public static Date jalaliToGregorian(YearMonthDate jDate) {
        int countKYears = (jDate.getYear() - 1348) / 4;
        int countAllYears = jDate.getYear() - 1348 - 1;
        int countNYears = countAllYears - countKYears;

        long lastSecond1348 = 6739200;
        long secondNYear = 31536000;
        long secondKYear = 31622400;
        long secondM31 = 2678400;
        long secondM30 = 2592000;
        long aHour = 3600000;
        long secondCurrentYear;
        long secondCurrentMonth;

        secondCurrentMonth = jDate.getDate() * 86400;
        int lastCompMonth = jDate.getMonth() - 1;
        if (lastCompMonth < 6) {
            secondCurrentYear = (secondM31 * lastCompMonth) + secondCurrentMonth;
        } else {
            secondCurrentYear = (secondM31 * 6) + (secondM30 * (lastCompMonth - 6)) + secondCurrentMonth;
        }
        long second = (secondKYear * countKYears) + (secondNYear * countNYears) + lastSecond1348 + secondCurrentYear;

//    //12600 is GMT+3:30

        second = second + (jDate.getHour() * 3600) + (jDate.getMinutes() * 60) + jDate.getSecond() - 12600;
        second *= 1000;

        //To calculate the clock change in the first six months of the year
        if (lastCompMonth < 6 && !(lastCompMonth == 5 && jDate.date == 31) && !(lastCompMonth == 0 && jDate.date == 1)) {
            second -= aHour;
        }

        Date date = new Date(second);
        return date;
    }

    public static class YearMonthDate {

        public YearMonthDate(int year, int month, int date) {
            this.year = year;
            this.month = month;
            this.date = date;
            hour = 0;
            Minutes = 0;
            Second = 0;
        }


        public YearMonthDate(int year, int month, String monthText, int date, String weekText, int hour, int minutes, int second) {
            this.year = year;
            this.month = month;
            this.monthText = monthText;
            this.date = date;
            this.weekText = weekText;
            this.hour = hour;
            Minutes = minutes;
            Second = second;
        }

        public YearMonthDate(int year, int month, int date, int hour, int minutes, int second) {
            this.year = year;
            this.month = month;
            this.date = date;
            this.hour = hour;
            Minutes = minutes;
            Second = second;
        }

        private int year;
        private int month;
        private String monthText;
        private int date;
        private String weekText;
        private int hour;
        private int Minutes;
        private int Second;

        public String getMonthText() {
            return monthText;
        }

        public void setMonthText(String monthText) {
            this.monthText = monthText;
        }

        public String getWeekText() {
            return weekText;
        }

        public void setWeekText(String weekText) {
            this.weekText = weekText;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinutes() {
            return Minutes;
        }

        public void setMinutes(int minutes) {
            Minutes = minutes;
        }

        public int getSecond() {
            return Second;
        }

        public void setSecond(int second) {
            Second = second;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDate() {
            return date;
        }

        public void setDate(int date) {
            this.date = date;
        }

        public String toString() {
            return getYear() + "/" + getMonth() + "/" + getDate();
        }
    }

    public static ArrayList<Date> listOfDatesBetween(Date date1, Date date2) {
        ArrayList<Date> dates = new ArrayList<>();
        long lengthOfDay = 86400000;
        for (long i = date1.getTime(); i <= date2.getTime(); i = i + lengthOfDay)
            dates.add(new Date(i));
        return dates;
    }

    public static ArrayList<Date> listOfDatesBetween(Date date, int afterDate) {
        ArrayList<Date> dates = new ArrayList<>();
        long lengthOfDay = 86400000;
        for (long i = 0; i < afterDate; i++)
            dates.add(new Date(date.getTime() + (lengthOfDay * i)));
        return dates;
    }

    public static ArrayList<Date> listOfDatesBetween(int beforeDate, Date date) {
        ArrayList<Date> dates = new ArrayList<>();
        long lengthOfDay = 86400000;
        for (long i = beforeDate; i >= 0; i--)
            dates.add(new Date(date.getTime() - (lengthOfDay * i)));
        return dates;
    }

    public static Date getBeforeDays(int count) {
        Date date = new Date();
        long x = date.getTime();
        date.setTime(x - (86400000l * count));
        return date;
    }

    public static Date getAfterDays(int count) {
        Date date = new Date();
        long x = date.getTime();
        date.setTime(x + (86400000l * count));
        return date;
    }

    public static Date getAfterDays(Date date, int count) {
        long x = date.getTime();
        Date date1 = new Date();
        date1.setTime(x + (86400000l * count));
        return date1;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date parseFormat(String date, String format) {
        try {
            DateFormat f;
            if (format == null) {
                f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            } else {
                f = new SimpleDateFormat(format);
            }
            Date d = null;
            f.setTimeZone(TimeZone.getTimeZone("GMT"));
            d = f.parse(date);
            DateFormat time = new SimpleDateFormat("HH:mm:ss");
            time.format(d);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param date <br>
     *             <b>return a value such as: <br>بعد از ظهر دوشنبه،30 دی، ساعت 18:00<b/><br>
     */
    public static String strPersianDate(Date date) {
        DateHelper util = new DateHelper();
        SolarCalendar sc = util.new SolarCalendar(date);
        String middle = ((date.getHours() > 12 && date.getHours() < 24) ? "بعد از ظهر" : "صبح");
        return String.format(new Locale("en_US"), "%s %s %02d %s ساعت %02d:%02d", middle, sc.strWeekDay, sc.date, sc.strMonth, date.getHours(), date.getMinutes());
    }

    /**
     * @param date <br>
     *             <b>return a value such as: <br>از دوشنبه 30 دی<b/><br>
     */
    public static String strPersianTwo(Date date) {
        DateHelper util = new DateHelper();
        SolarCalendar sc = util.new SolarCalendar(date);
        return String.format(new Locale("en_US"), "%s %02d %s", sc.strWeekDay, sc.date, sc.strMonth);
    }

    /**
     * @param date <br>
     *             <b>return a value such as: <br>دوشنبه 30 دی<b/><br>
     */
    public static String strPersianTree(Date date) {
        DateHelper util = new DateHelper();
        SolarCalendar sc = util.new SolarCalendar(date);
        return String.format(new Locale("en_US"), "%s %02d %s", sc.strWeekDay, sc.date, sc.strMonth);
    }

    /**
     * @param date <br>
     *             <b>return a value such as: <br>دوشنبه 18:05<b/><br>
     */
    public static String strPersianFour(Date date) {
        try {
            DateHelper util = new DateHelper();
            SolarCalendar sc = util.new SolarCalendar(date);
            return String.format(new Locale("en_US"), "%02d:%02d %s", date.getHours(), date.getMinutes(), sc.strWeekDay);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param date <br>
     *             <b>return a value such as: <br>18:05<b/><br>
     */
    public static String strPersianFour1(Date date) {
        try {
            DateHelper util = new DateHelper();
            SolarCalendar sc = util.new SolarCalendar(date);
            return String.format(new Locale("en_US"), "%02d:%02d", date.getHours(), date.getMinutes());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param date <br>
     *             <b>return a value such as: <br>1398/10/30 <b/><br>
     */
    public static String strPersianSeven(Date date) {
        try {
            DateHelper util = new DateHelper();
            SolarCalendar sc = util.new SolarCalendar(date);
            return String.format(new Locale("en_US"), "%04d/%02d/%02d", sc.year, sc.month, sc.date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param date <br>
     *             <b>return a value such as: <br>30 دی ساعت 18:07<b/><br>
     */
    public static String strPersianEghit(Date date) {
        try {
            DateHelper util = new DateHelper();
            SolarCalendar sc = util.new SolarCalendar(date);
            return String.format(new Locale("en_US"), "%02d %s ساعت %02d:%02d", sc.date, sc.strMonth, date.getHours(), date.getMinutes());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param date <br>
     *             <b>return a value such as: <br>30 دی <b/><br>
     */
    public static String strPersianTen(Date date) {
        DateHelper util = new DateHelper();
        SolarCalendar sc = util.new SolarCalendar(date);
        return String.format(new Locale("en_US"), "%02d %s", sc.date, sc.strMonth);
    }

    /**
     * @param d <br>
     *          <b>return a value such as: <br>2020-01-20<b/><br>
     */
    public static String strPersianFive(long d) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(d);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //Here you say to java the initial timezone. This is the secret

            return sdf.format(calendar.getTime());

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * @param d <br>
     *          <b>return a value such as: <br>2020-01-20<b/><br>
     */
    public static String strPersianSix(Date d) {
        return strPersianFive(d.getTime());
    }

    public static String parseFormatToString(String date) {
        return strPersianTree(parseFormat(date, null));
    }

    public static String parseFormat(String date) {
        return strPersianFour1(parseFormat(date, null));
    }

    /**
     * @param date format input is : yyyy/MM/dd
     * @param time format input is : hh:mm:ss
     * @return string text is return
     */
    public static String parseDate(String date, String time) {

        String[] date1 = date.split("/");
        String[] time1 = time.split(":");

        Date d = DateHelper.jalaliToGregorian(new DateHelper.YearMonthDate(
                Integer.parseInt(date1[0])
                , Integer.parseInt(date1[1])
                , Integer.parseInt(date1[2])
                , Integer.parseInt(time1[0])
                , Integer.parseInt(time1[1])
                , Integer.parseInt(time1[2])));
        return DateHelper.strPersianDate(d);
    }

    public static String parseStrDate(String date, String time) {
        try {
            String[] date1 = date.split("/");
            String[] time1 = time.split(":");

            Date d = DateHelper.jalaliToGregorian(new YearMonthDate(
                    Integer.parseInt(date1[0])
                    , Integer.parseInt(date1[1])
                    , Integer.parseInt(date1[2])
                    , Integer.parseInt(time1[0])
                    , Integer.parseInt(time1[1])
                    , Integer.parseInt(time1[2])));
            return DateHelper.strPersianDate(d);
        } catch (Exception e) {
            return DateHelper.strPersianDate(new Date());
        }
    }

    /**
     * jalali date input
     *
     * @param date intput type 1373/02/09
     * @return
     */
    public static Date parseDate(String date) {
        String[] date1 = date.split("/");

        Date d = DateHelper.jalaliToGregorian(new YearMonthDate(
                Integer.parseInt(date1[0])
                , Integer.parseInt(date1[1])
                , Integer.parseInt(date1[2])));
        return d;
    }
}
