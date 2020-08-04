package its.traffic.flow.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间工具类
 */
public class DateTimeUtils {
    /**
     * 得到增加minute时间的时间戳
     *
     * @param minute 分钟
     * @return 时间戳
     */
    public static String getTimeByAddMinute(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        long unixTimestamp = calendar.getTimeInMillis();
        return unixTimestamp + "";
    };
    /**
     * 通过日期获取时间戳
     *
     * @param dateStr 日期字符串
     * @return 时间戳字符串
     */
    public static String getTimeStrByDate(String dateStr, String pattern) {
        return getTimeStampByDate(dateStr, pattern) + "";
    }

    /**
     * 通过日期获取时间戳
     *
     * @param dateStr 日期字符串
     * @return 失败返回-1
     */
    public static long getTimeStampByDate(String dateStr, String pattern) {
        long timeStamp = -1;
        try {
            DateFormat dateFormat = new SimpleDateFormat(pattern);
            timeStamp = dateFormat.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp;
    }

    /**
     * 修改时间格式
     *
     * @return String 时间  年-月-日 时：分：秒
     */
    public static String dateFormat(Date date,String s) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(s);//可以方便地修改日期格式
        String hehe = dateFormat.format(date);
        return hehe;
    }
    /**
     * 格式化时间戳
     *
     * @param millis  毫秒
     * @param pattern 匹配格式
     * @return 格式化的日期字符串
     */
    public static String dataFormat(long millis, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(millis);
    }

    /**
     * 判断时间是否在时间段内 以时为单位
     *
     * @param strDate      当前时间 yyyy-MM-dd HH:mm:ss
     * @param strDateBegin 开始时间 00:00:00
     * @param strDateEnd   结束时间 00:05:00
     * @return
     */
    public static boolean isInDate(String strDate, String strDateBegin, String strDateEnd) {
        // 截取当前时间时
        int strDateH = Integer.parseInt(strDate.substring(0, 2));
        // 截取开始时间时
        int strDateBeginH = Integer.parseInt(strDateBegin.substring(0, 2));
        // 截取结束时间时
        int strDateEndH = Integer.parseInt(strDateEnd.substring(0, 2));
        if (strDateH >= strDateBeginH || strDateH <= strDateEndH) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前时间
     *
     * @return String 时间  年-月-日 时：分：秒
     */
    public static String getSystemTime(String s) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(s);//可以方便地修改日期格式
        String hehe = dateFormat.format(now);
        return hehe;
    }




    /**
     * 获取当前几分钟
     *
     * @param minute
     * @return
     */
    public static String getTimeByMinute(int minute,String str) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, minute);

        return new SimpleDateFormat(str).format(calendar.getTime());

    }

    /**
     * 获取当前时间的前一天
     *
     * @return
     */
    public static String getTimeByDay(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        date = calendar.getTime();
        return sdf.format(date);
    }



    /**
     * 获取当前时间的前N天
     *
     * @return
     */
    public static Date getTimeByNDay(Date date,String str,int n) {
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, n);
        date = calendar.getTime();
        return date;
    }

    /**
     * 获取当前时间的前N天
     *
     * @return
     */
    public static String getTimeByNDay(String date,String str,int n) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse(date));
        calendar.add(Calendar.DAY_OF_MONTH, n);
        Date time = calendar.getTime();
        return sdf.format(time);
    }




    /**
     * 获取时间的前一个小时
     *
     * @return
     */
    public static String getTimeByHour(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date parse = sdf.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parse);
        calendar.add(Calendar.MINUTE, -55);
        parse = calendar.getTime();
        return sdf.format(parse);
    }


    public static String getTimeByMinute() {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, -5);

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

    }

    /**
     * 将传过来的时间   小时调为两位数  取整  分秒为0
     *
     * @param key
     * @return
     */
    public static String getTimeString(String key) {
        String rq = key.substring(0, 11);
        final Integer s = Integer.valueOf(key.substring(11, 13));
        String hour = null;
        if (s.toString().length() < 2) {
            hour = "0" + s + ":00:00";
        } else {
            hour = s.toString() + ":00:00";
        }
        String time = rq + hour;
        return time;
    }


    /**
     * 相同天 相差几个小时
     *
     * @param StartDate
     * @param endDate
     * @return
     */
    public static int getHours(String StartDate ,String endDate) {
        String startHour = StartDate.split(" ")[1].split(":")[0];
        String endHour = endDate.split(" ")[1].split(":")[0];

        int hours = Integer.valueOf(endHour) - Integer.valueOf(startHour);
        return hours;
    }
    
    /*
     * 把日期该月该天 变成上个月第1天 例如4月20日变为3月1日 和3月20日
     * 返回得到两个值 
     * 第一个该日期上个月1日
     * 第二个该日期上个月当前日
     * 
     * */
    public static String[] getLastMonthLastDay(String str) {
    	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
	
		try {
			calendar.setTime(sdf2.parse(str));
			calendar.add(Calendar.MONTH, -1);
			String date1 = sdf3.format(calendar.getTime());
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			String date2 = sdf3.format(calendar.getTime());
			
			return new String[]{date2,date1}; 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
    }
    
    /*
     * 求该月的一号
     * */
    public static String getLastMonth(String str) {
    	SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);
	
		try {
			calendar.setTime(sdf2.parse(str));
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			String date2 = sdf3.format(calendar.getTime());
			
			return date2; 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

    }

    /**
     * 两个日期之间的所有日期
     * @param startTime
     * @param endTime
     * @return
     */
    public static Map<String, Boolean> getDays(String startTime, String endTime) {

        // 返回的日期集合
//        List<String> days = new ArrayList<String>();
        Map<String,Boolean> map = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
//                days.add(dateFormat.format(tempStart.getTime()));
                map.put(dateFormat.format(tempStart.getTime()),true);
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 两个日期之间的所有日期
     * @param start
     * @param end
     * @return
     */
    public static Map<String, Boolean> getDays(Date start, Date end) {

        // 返回的日期集合
//        List<String> days = new ArrayList<String>();
        Map<String,Boolean> days = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        Calendar tempEnd = Calendar.getInstance();
        tempEnd.setTime(end);
        tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
        while (tempStart.before(tempEnd)) {
//                days.add(dateFormat.format(tempStart.getTime()));
//            System.out.println(tempStart.getTime());
            days.put(dateFormat.format(tempStart.getTime()),true);
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
        }

        return days;
    }

    /**
     * 获取某年的所有周日
     * @param year
     * @return
     */
    public static Map<String, String> getAllWeek(int year) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, String> map = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.set(year, 0, 1);
        Calendar c2 = Calendar.getInstance();
        c2.set(year + 1, 0, 1);
        while (c.compareTo(c2) < 0) {
            if (c.get(Calendar.DAY_OF_WEEK)  == Calendar.SUNDAY) {

                String month = c.get(Calendar.MONTH) + 1 +"";
                String day = c.get(Calendar.DAY_OF_MONTH)+"";
                if(Integer.valueOf(month) < 10){
                    month = "0"+month;
                }
                if(Integer.valueOf(day) < 10){
                    day = "0"+day;
                }
                map.put(c.get(Calendar.YEAR) + "-" + month + "-" + day, c.get(Calendar.MONTH) + 1+"月");
            }
            // 日期+1
            c.add(Calendar.DATE, 1);
        }
        return map;
    }

    /**
     * 获取某年某月所有周日
     * @param year
     * @return
     */
    public static Map<String, String> getAllWeek(int year,int month) {
        Map<String, String> map = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.set(year, 0, 1);
        Calendar c2 = Calendar.getInstance();
        c2.set(year + 1, 0, 1);
        while (c.compareTo(c2) < 0) {
            if (c.get(Calendar.DAY_OF_WEEK)  == Calendar.SUNDAY) {
                if(c.get(Calendar.MONTH) + 1 == month) {
                    map.put(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
                            + c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1+"月");
                }

            }
            // 日期+1
            c.add(Calendar.DATE, 1);
        }
        return map;
    }

    /**
     * 获取当天  前n天或者后n天的日期
     * @param date
     * @param nday
     * @param fx
     * @return
     * @throws ParseException
     */
    public static List<String> getNextNDay(String date,int nday,int fx) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime( sdf.parse(date));
        List<String> list = new ArrayList<String>();
        //如果是获取后n天的日期，返回包含当天
        if(fx > 0) {
            list.add(date);
        }
        for(int i = 1; i<=nday;i++) {
            c.add(Calendar.DAY_OF_WEEK, fx);
            Date d = c.getTime();
            String day = sdf.format(d);
            list.add(day);
//		     System.out.println(day);
        }
        return list;
    }

    /**
     * 两个月份之间所有的月份
     * @param minDate
     * @param maxDate
     * @return
     * @throws ParseException
     */
    public static Map<String, String> getMonthBetween(String minDate, String maxDate) throws ParseException {
//        ArrayList<String> result = new ArrayList<String>();
        Map<String,String> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();

        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

        Calendar curr = min;
        while (curr.before(max)) {
            map.put(sdf.format(curr.getTime()),"1");
//            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }

        return map;
    }


    /**
     * 获取时间片段
     * @param date
     * @return
     * @throws ParseException
     */
    public static Map<String,String> getTimePart(String date) throws ParseException{
        String[] split = date.split(" ");
        Integer hour = Integer.valueOf(split[1].split(":")[0]);
        Integer min = Integer.valueOf(split[1].split(":")[1]);
        String sec = split[1].split(":")[2];

        if( min%5 != 0){
            min = min + (5 - min%5);
            if(min==60){
                min = 0;
                hour = hour +1;
            }
        }

        String min_str = min +"";
        if(min < 10){
            min_str = "0"+min;
        }
        String hour_str = hour +"";
        if(hour < 10){
            hour_str = "0"+hour;
        }

        String time = split[0] + " " + hour_str + ":" + min_str + ":" + sec;
        String time_before = getTimeByHour(time);

        Map<String,String> map = new HashMap<>();
        map.put("time_before",time_before);
        map.put("time",time);

        return map;
    }

    /**
     * 将yyyyMMdd日期格式 转换为yyyy-MM-dd
     * @param date
     * @return
     * @throws ParseException
     */
    public static String replaceDate(Integer date)throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");//可以方便地修改日期格式
        Date parse = sdf.parse(date+"");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");//可以方便地修改日期格式
        return sdf2.format(parse);
    }


    /**
     * 计算时间
     * @param startTime
     * @param endTime
     * @return
     */
    public static int calcTime_hour_min(Integer startTime,Integer endTime) {
        String transStringTime = transTime(startTime);
        String transEndTime = transTime(endTime);

        String[] stratSplit = transStringTime.split(":");
        String[] endSplit = transEndTime.split(":");

        int min = (Integer.valueOf(endSplit[0]) - Integer.valueOf(stratSplit[0])) * 60;

        min = min + (Integer.valueOf(endSplit[1]) - Integer.valueOf(stratSplit[1]));
        return min;
    }



    /**
     * 转换时间
     * @param time
     * @return
     */
    public static String transTime(Integer time) {
        String timeString = time.toString();
        int length = time.toString().length();
        String sj = "";
        if(length == 1) {
            sj = "00:0"+timeString;
        }else if(length == 2) {
            sj = "00:"+timeString;
        }else if(length == 3) {
            sj = "0"+timeString.substring(0,1)+":"+timeString.substring(1,3);
        }else if(length == 4) {
            sj = timeString.substring(0,2)+":"+timeString.substring(2,4);
        }
        return sj;
    }

    public static void main(String[] args)  throws ParseException {
        getTimePart("2020-04-24 12:11:32");
    }
}
