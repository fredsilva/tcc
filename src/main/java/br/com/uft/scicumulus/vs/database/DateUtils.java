package br.com.uft.scicumulus.vs.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils
{
  public static final int PERID_DIARIO = -1;
  public static final int PERID_ATEMPORAL = 15;
  public static final int PERID_MENSAL = 1;
  public static final int PERID_TRIMESTRAL = 3;
  public static final int PERID_QUADRIMESTRAL = 4;
  public static final int PERID_SEMESTRAL = 6;
  public static final int PERID_ANUAL = 12;
  private static SimpleDateFormat formatdb = new SimpleDateFormat("yyyy-MM-dd");

  private static SimpleDateFormat format1x = new SimpleDateFormat("dd/MM/yyyy");

  private static SimpleDateFormat format1 = new SimpleDateFormat("yyyy MM");
  private static SimpleDateFormat format12 = new SimpleDateFormat("yyyy");

  public static Date makeDate(int year, int month, int day)
  {
    BigDate big = new BigDate(year, month, day);
    return big.getLocalDate();
  }

  public static Date extractDate(String value)
  {
    if (value == null)
      return null;
    try
    {
      return format1x.parse(value);
    } catch (ParseException e) {
      e.printStackTrace();
    }return null;
  }

  public static Date extractDBDate(String value)
  {
    if (value == null)
      return null;
    try
    {
      return formatdb.parse(value);
    } catch (ParseException e) {
      e.printStackTrace();
    }return null;
  }

  public static String formatDBDate(Date date)
  {
    return formatdb.format(date);
  }

  public static String formatDateFmt(Date date, String fmt) {
    SimpleDateFormat fmtDate = new SimpleDateFormat(fmt);
    return fmtDate.format(date);
  }

  public static String formatDate(Date date, int perID)
  {
    if (date != null) {
      date = convertDate(date, perID);
      switch (perID) {
      case -1:
        return format1x.format(date);
      case 1:
        return format1.format(date);
      case 3:
        return format1.format(date);
      case 4:
        return format1.format(date);
      case 6:
        return format1.format(date);
      case 12:
        return format12.format(date);
      case 0:
      case 2:
      case 5:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11: }  } return "";
  }

  public static Date convertDate(Date date, int perID)
  {
    Calendar call = Calendar.getInstance();
    call.setTime(date);
    int year = call.get(1); int month = call.get(2) + 1; int day = call.get(5);
    if (perID > 1) {
      switch (perID) {
      case 1:
      case 3:
      case 4:
      case 6:
        month = (month + perID - 1) / perID * perID - (perID - 1);
        break;
      case 2:
      case 5:
      default:
        month = 1;
      }
    }

    if (perID != -1) {
      day = 1;
    }
    date = makeDate(year, month, day);

    return date;
  }

  public static Date incData(Date date, int perID, int qtd)
  {
    date = convertDate(date, perID);

    Calendar call = new GregorianCalendar();
    call.setTime(date);
    if (perID == -1) {
      call.add(5, qtd);
      return call.getTime();
    }
    call.add(2, perID * qtd);
    date = call.getTime();
    return date;
  }

  public static int getDays(Date finish, Date start) {
    BigDate bigFinish = new BigDate(finish, TimeZone.getDefault());
    BigDate bigStart = new BigDate(start, TimeZone.getDefault());
    return bigFinish.getOrdinal() - bigStart.getOrdinal();
  }

  public static int getDayOfWeek(Date data) {
    BigDate bigDate = new BigDate(data, TimeZone.getDefault());
    return bigDate.getDayOfWeek();
  }

  public static int getMonths(Date finish, Date start) {
    Calendar call = Calendar.getInstance();
    call.setTime(finish);

    int YearF = call.get(1); int MonthF = call.get(2) + 1;
    call.setTime(start);

    int YearS = call.get(1); int MonthS = call.get(2) + 1;
    return (YearF - YearS) * 12 + (MonthF - MonthS);
  }
}