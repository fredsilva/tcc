package br.com.uft.scicumulus.vs.database;

//import vs.database.StringTools;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.TimeZone;

public final class BigDate
        implements Cloneable, Serializable, Comparable {

    static final boolean DEBUGGING = false;
    public static final boolean isBritish = true;
    private static final int AD_epochAdjustment;
    private static final int BC_epochAdjustment;
    public static final int BYPASSCHECK = 1;
    public static final int CHECK = 0;
    private static final int GC_firstDD;
    private static final int GC_firstDec_31;
    private static final int GC_firstMM;
    private static final int GC_firstOrdinal;
    private static final int GC_firstYYYY;
    private static final int Jan_01_0001;
    private static final int Jan_01_0001BC;
    private static final int Jan_01_0004;
    private static final int Jan_01_Leap100RuleYear;
    private static final int Jan_01_Leap400RuleYear;
    private static final int Leap100RuleYYYY;
    private static final int Leap400RuleYYYY;
    public static final int MAX_ORDINAL;
    public static final int MAX_YEAR = 999999;
    public static final int MIN_ORDINAL;
    public static final int MIN_YEAR = -999999;
    private static final int missingDays;
    private static final int MondayIsZeroAdjustment = 3;
    public static final int NORMALISE = 2;
    public static final int NORMALIZE;
    public static final int NULL_ORDINAL = -2147483648;
    private static final int OJC_lastDD;
    private static final int OJC_lastMM;
    private static final int OJC_lastYYYY;
    private static final int SundayIsZeroAdjustment = 4;
    public static final long NULL_TIMESTAMP = -9223372036854775808L;
    static final long serialVersionUID = 34L;
    private static final String[] shortDayName = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
    private static final int[] leap_daysInYearPriorToMonthTable = {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335};
    private static int[] leap_dddToMMTable;
    private static final int[] usual_daysInYearPriorToMonthTable = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    private static final int[] usual_DaysPerMonthTable = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static int[] usual_dddToMMTable;
    protected transient int dd = 0;
    protected transient int mm = 0;
    protected int ordinal = -2147483648;
    protected transient int yyyy = 0;

    public static BigDate UTCToday() {
        return new BigDate((int) (System.currentTimeMillis() / 86400000L));
    }

    public static int[] age(BigDate birthDate, BigDate asof) {
        if (birthDate.getOrdinal() >= asof.getOrdinal()) {
            return new int[]{0, 0, 0};
        }
        int birthYYYY = birthDate.getYYYY();
        int birthMM = birthDate.getMM();
        int birthDD = birthDate.getDD();

        int asofYYYY = asof.getYYYY();
        int asofMM = asof.getMM();
        int asofDD = asof.getDD();

        int ageInYears = asofYYYY - birthYYYY;
        int ageInMonths = asofMM - birthMM;
        int ageInDays = asofDD - birthDD;

        if (ageInDays < 0) {
            ageInDays += daysInMonth(birthMM, birthYYYY);
            ageInMonths--;
        }

        if (ageInMonths < 0) {
            ageInMonths += 12;
            ageInYears--;
        }
        if ((birthYYYY < 0) && (asofYYYY > 0)) {
            ageInYears--;
        }

        if (ageInYears < 0) {
            ageInYears = 0;
            ageInMonths = 0;
            ageInDays = 0;
        }
        int[] result = new int[3];
        result[0] = ageInYears;
        result[1] = ageInMonths;
        result[2] = ageInDays;
        return result;
    }

    public static int calendarDayOfWeek(int ordinal) {
        return dayOfWeek(ordinal) + 1;
    }

    public static int dayOfWeek(int ordinal) {
        return ordinal == -2147483648 ? 0 : (ordinal + 4 - MIN_ORDINAL) % 7;
    }

    public static int daysInMonth(int mm, boolean leap) {
        if (mm != 2) {
            return usual_DaysPerMonthTable[(mm - 1)];
        }

        return leap ? 29 : 28;
    }

    public static int daysInMonth(int mm, int yyyy) {
        if (mm != 2) {
            return usual_DaysPerMonthTable[(mm - 1)];
        }

        return isLeap(yyyy) ? 29 : 28;
    }

    public static int flooredMulDiv(int multiplicand, int multiplier, int divisor) {
        long result = multiplicand * multiplier;
        if (result >= 0L) {
            return (int) (result / divisor);
        }

        return (int) ((result - divisor + 1L) / divisor);
    }

    public static String getCopyright() {
        return "BigDate 4.9 freeware copyright (c) 1997-2008 Roedy Green, Canadian Mind Products, http://mindprod.com roedyg@mindprod.com";
    }

    public static boolean isLeap(int yyyy) {
        if (yyyy < Leap100RuleYYYY) {
            if (yyyy < 0) {
                return (yyyy + 1 & 0x3) == 0;
            }

            return (yyyy & 0x3) == 0;
        }

        if ((yyyy & 0x3) != 0) {
            return false;
        }
        if (yyyy % 100 != 0) {
            return true;
        }
        if (yyyy < Leap400RuleYYYY) {
            return false;
        }
        return yyyy % 400 == 0;
    }

    public static boolean isValid(String yyyy_mm_dd) {
        try {
            if (yyyy_mm_dd.length() != 10) {
                return false;
            }
            int yyyy = Integer.parseInt(yyyy_mm_dd.substring(0, 4));
            int mm = Integer.parseInt(yyyy_mm_dd.substring(5, 7));
            int dd = Integer.parseInt(yyyy_mm_dd.substring(8, 10));
            return isValid(yyyy, mm, dd);
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public static boolean isValid(int yyyy, int mm, int dd) {
        if (yyyy == 0) {
            return (mm == 0) && (dd == 0);
        }
        if ((yyyy < -999999) || (yyyy > 999999) || (mm < 1) || (mm > 12) || (dd < 1) || (dd > 31)) {
            return false;
        }

        if ((yyyy == OJC_lastYYYY) && (mm == OJC_lastMM) && (OJC_lastDD < dd) && (dd < GC_firstDD)) {
            return false;
        }
        return dd <= daysInMonth(mm, yyyy);
    }

    public static int isoDayOfWeek(int ordinal) {
        return ordinal == -2147483648 ? 0 : (ordinal + 3 - MIN_ORDINAL) % 7 + 1;
    }

    public static BigDate localToday() {
        return today(TimeZone.getDefault());
    }

    public static int nthXXXDay(int which, int dayOfWeek, int yyyy, int mm) {
        int dayOfWeekOf1st = dayOfWeek(toOrdinal(yyyy, mm, 1));

        int dayOfMonthOfFirstDesiredDay = (dayOfWeek - dayOfWeekOf1st + 7) % 7 + 1;

        int dayOfMonthOfNthDesiredDay = dayOfMonthOfFirstDesiredDay + (which - 1) * 7;

        if ((which >= 5) && (dayOfMonthOfNthDesiredDay > daysInMonth(mm, yyyy))) {
            dayOfMonthOfNthDesiredDay -= 7;
        }
        return dayOfMonthOfNthDesiredDay;
    }

    public static int ordinalOfnthXXXDay(int which, int dayOfWeek, int yyyy, int mm) {
        int dayOfMonthOfNthDesiredDay = nthXXXDay(which, dayOfWeek, yyyy, mm);
        return toOrdinal(yyyy, mm, dayOfMonthOfNthDesiredDay);
    }

    public static int toOrdinal(int yyyy, int mm, int dd) {
        if ((yyyy == 0) && (mm == 0) && (dd == 0)) {
            return -2147483648;
        }

        int missingDayAdjust = (yyyy == OJC_lastYYYY) && (((mm == OJC_lastMM) && (dd > OJC_lastDD)) || (mm > OJC_lastMM)) ? missingDays : 0;

        return jan01OfYear(yyyy) + daysInYearPriorToMonth(mm, isLeap(yyyy)) - missingDayAdjust + dd - 1;
    }

    public static BigDate today(TimeZone timeZone) {
        BigDate d = new BigDate();
        d.setDateAtTime(System.currentTimeMillis(), timeZone);
        return d;
    }

    public BigDate() {
    }

    public BigDate(double prolepticJulianDay) {
        setOrdinal((int) ((prolepticJulianDay + 0.5D) - 2440588L));
    }

    public BigDate(int ordinal) {
        set(ordinal);
    }

    public BigDate(String yyyy_mm_dd) {
        try {
            if (yyyy_mm_dd.length() != 10) {
                throw new IllegalArgumentException("invalid date: " + yyyy_mm_dd);
            }

            int yyyy = Integer.parseInt(yyyy_mm_dd.substring(0, 4));
            int mm = Integer.parseInt(yyyy_mm_dd.substring(5, 7));
            int dd = Integer.parseInt(yyyy_mm_dd.substring(8, 10));
            set(yyyy, mm, dd, 0);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid date: " + yyyy_mm_dd);
        }
    }

    public BigDate(BigDate b) {
        this.ordinal = b.ordinal;
        this.yyyy = b.yyyy;
        this.mm = b.mm;
        this.dd = b.dd;
    }

    public BigDate(Date utc, TimeZone timeZone) {
        setDateAtTime(utc.getTime(), timeZone);
    }

    public BigDate(int yyyy, int mm, int dd) {
        set(yyyy, mm, dd, 0);
    }

    public BigDate(int yyyy, int mm, int dd, int how) {
        set(yyyy, mm, dd, how);
    }

    public final void addDays(int days) {
        if (days == 0) {
            return;
        }
        this.ordinal += days;
        toGregorian();
    }

    public int calendarDayOfWeek() {
        return calendarDayOfWeek(this.ordinal);
    }

    public final int compareTo(Object anotherBigDate) {
        return this.ordinal - ((BigDate) anotherBigDate).ordinal;
    }

    public int dayOfWeek() {
        return dayOfWeek(this.ordinal);
    }

    public final boolean equals(Object d) {
        if (d == this) {
            return true;
        }
        if (!(d instanceof BigDate)) {
            return false;
        }
        return this.ordinal == ((BigDate) d).getOrdinal();
    }

    public final int getCalendarDayOfWeek() {
        return calendarDayOfWeek(this.ordinal);
    }

    public final int getDD() {
        return this.dd;
    }

    public final int getDDD() {
        return this.ordinal == -2147483648 ? 0 : this.ordinal - jan01OfYear(this.yyyy) + 1;
    }

    public final Date getDate(TimeZone timeZone) {
        return this.ordinal == -2147483648 ? null : new Date(getTimeStamp(timeZone));
    }

    public final int getDayOfWeek() {
        return dayOfWeek(this.ordinal);
    }

    public final int getISODayOfWeek() {
        return isoDayOfWeek(this.ordinal);
    }

    public final int getISOWeekNumber() {
        if (this.ordinal < Jan_01_Leap100RuleYear) {
            return 0;
        }
        int jan04Ordinal = jan01OfYear(this.yyyy) + 3;
        int jan04DayOfWeek = (jan04Ordinal + 3 - MIN_ORDINAL) % 7;

        int week1StartOrdinal = jan04Ordinal - jan04DayOfWeek;
        if (this.ordinal < week1StartOrdinal) {
            jan04Ordinal = jan01OfYear(this.yyyy - 1) + 3;
            jan04DayOfWeek = (jan04Ordinal + 3 - MIN_ORDINAL) % 7;

            week1StartOrdinal = jan04Ordinal - jan04DayOfWeek;
        } else if (this.mm == 12) {
            jan04Ordinal = jan01OfYear(this.yyyy + 1) + 3;
            jan04DayOfWeek = (jan04Ordinal + 3 - MIN_ORDINAL) % 7;

            int week1StartNextOrdinal = jan04Ordinal - jan04DayOfWeek;
            if (this.ordinal >= week1StartNextOrdinal) {
                week1StartOrdinal = week1StartNextOrdinal;
            }
        }
        return (this.ordinal - week1StartOrdinal) / 7 + 1;
    }

    public final Date getLocalDate() {
        return this.ordinal == -2147483648 ? null : new Date(getLocalTimeStamp());
    }

    public final long getLocalTimeStamp() {
        return getTimeStamp(TimeZone.getDefault());
    }

    public final int getMM() {
        return this.mm;
    }

    public final int getOrdinal() {
        return this.ordinal;
    }

    public final double getProplepticJulianDay() {
        if (this.ordinal == -2147483648) {
            return 4.9E-324D;
        }

        return this.ordinal + 2440588L;
    }

    public final int getSeason() {
        return (this.mm + 9) % 12 / 3;
    }

    public final long getTimeStamp(TimeZone timeZone) {
        if (this.ordinal == -2147483648) {
            return -9223372036854775808L;
        }

        int offsetInMillis = timeZone.getOffset(this.ordinal >= Jan_01_0001 ? 1 : 0, this.yyyy, this.mm - 1, this.dd, getCalendarDayOfWeek(), 0);

        return this.ordinal * 86400000L - offsetInMillis;
    }

    public final Date getUTCDate() {
        return this.ordinal == -2147483648 ? null : new Date(getUTCTimeStamp());
    }

    public final long getUTCTimeStamp() {
        return this.ordinal == -2147483648 ? -9223372036854775808L : this.ordinal * 86400000L;
    }

    public final int getWeekNumber() {
        if (this.ordinal < Jan_01_Leap100RuleYear) {
            return 0;
        }
        int jan01 = jan01OfYear(this.yyyy);
        int jan01DayOfWeek = dayOfWeek(jan01);

        int sundayOnOrBeforeJan01Ordinal = jan01 - jan01DayOfWeek;
        return (this.ordinal - sundayOnOrBeforeJan01Ordinal) / 7 + 1;
    }

    public final int getYYYY() {
        return this.yyyy;
    }

    public final int hashCode() {
        return this.ordinal;
    }

    public BigDate nearestXXXDay(int dayOfWeek) {
        int diff = dayOfWeek - dayOfWeek();
        if (diff >= 4) {
            diff -= 7;
        } else if (diff <= -4) {
            diff += 7;
        }
        return new BigDate(this.ordinal + diff);
    }

    public final void set(int ordinal) {
        if (this.ordinal == ordinal) {
            return;
        }
        this.ordinal = ordinal;
        toGregorian();
    }

    public final void set(int yyyy, int mm, int dd) {
        set(yyyy, mm, dd, 0);
    }

    public final void set(int yyyy, int mm, int dd, int how) {
        if ((this.yyyy == yyyy) && (this.mm == mm) && (this.dd == dd)) {
            return;
        }
        this.yyyy = yyyy;
        this.mm = mm;
        this.dd = dd;
        switch (how) {
            case 0:
                if (!isValid(yyyy, mm, dd)) {
                    throw new IllegalArgumentException("invalid date: " + yyyy + "/" + mm + "/" + dd);
                }

                break;
            case 2:
                normalise();
                break;
            case 1:
        }

        toOrdinal();
    }

    public void setDateAtTime(long utcTimestamp, TimeZone timeZone) {
        setOrdinal((int) (utcTimestamp / 86400000L));

        int offsetInMillis = timeZone.getOffset(getOrdinal() >= Jan_01_0001 ? 1 : 0, getYYYY(), getMM() - 1, getDD(), getCalendarDayOfWeek(), (int) (utcTimestamp % 86400000L));

        setOrdinal((int) ((utcTimestamp + offsetInMillis) / 86400000L));
    }

    public final void setOrdinal(int ordinal) {
        if (this.ordinal == ordinal) {
            return;
        }
        this.ordinal = ordinal;
        toGregorian();
    }

    public String toDowMMDDYY() {
        if (this.ordinal == -2147483648) {
            return "";
        }
        return shortDayName[getDayOfWeek()] + " " + StringTools.toLZ(this.mm, 2) + "/" + StringTools.toLZ(this.dd, 2) + "/" + StringTools.toLZ(this.yyyy % 100, 2);
    }

    public String toString() {
        if (this.ordinal == -2147483648) {
            return "";
        }
        String result;
        if ((-999 <= this.yyyy) && (this.yyyy <= 9999)) {
            result = StringTools.toLZ(this.yyyy, 4);
        } else {
            result = Integer.toString(this.yyyy);
        }
        result = result + "-" + StringTools.toLZ(this.mm, 2) + "-" + StringTools.toLZ(this.dd, 2);

        return result;
    }

    protected static int daysInYearPriorToMonth(int mm, boolean leap) {
        return leap ? leap_daysInYearPriorToMonthTable[(mm - 1)] : usual_daysInYearPriorToMonthTable[(mm - 1)];
    }

    protected static int dddToMM(int ddd, boolean leap) {
        return leap ? leap_dddToMMTable[(ddd - 1)] : usual_dddToMMTable[(ddd - 1)];
    }

    protected static int jan01OfYear(int yyyy) {
        if (yyyy < 0) {
            int leapAdjustment = (3 - yyyy) / 4;
            return yyyy * 365 - leapAdjustment + BC_epochAdjustment;
        }

        int leapAdjustment = (yyyy - 1) / 4;

        int missingDayAdjust = yyyy > GC_firstYYYY ? missingDays : 0;

        if (yyyy > Leap100RuleYYYY) {
            leapAdjustment -= (yyyy - Leap100RuleYYYY + 99) / 100;
        }
        if (yyyy > Leap400RuleYYYY) {
            leapAdjustment += (yyyy - Leap400RuleYYYY + 399) / 400;
        }

        return yyyy * 365 + leapAdjustment - missingDayAdjust + AD_epochAdjustment;
    }

    protected final void normalise() {
        if (isValid(this.yyyy, this.mm, this.dd)) {
            return;
        }
        if (this.mm > 12) {
            this.yyyy += (this.mm - 1) / 12;
            this.mm = ((this.mm - 1) % 12 + 1);
            if (!isValid(this.yyyy, this.mm, this.dd));
        } else if (this.mm <= 0) {
            this.yyyy -= -this.mm / 12 + 1;
            this.mm = (12 - -this.mm % 12);
            if (isValid(this.yyyy, this.mm, this.dd)) {
                return;
            }
        }
        if (isValid(this.yyyy, this.mm, 1)) {
            int olddd = this.dd;
            this.dd = 1;
            toOrdinal();
            this.ordinal += olddd - 1;
            toGregorian();
            if (isValid(this.yyyy, this.mm, this.dd)) {
                return;
            }
        }

        throw new IllegalArgumentException("date cannot be normalised: " + this.yyyy + "/" + this.mm + "/" + this.dd);
    }

    private void readObject(ObjectInputStream s)
            throws ClassNotFoundException, IOException {
        s.defaultReadObject();
        try {
            toGregorian();
        } catch (IllegalArgumentException e) {
            throw new IOException("bad serialized BigDate");
        }
    }

    protected final void toGregorian() {
        if (this.ordinal == -2147483648) {
            this.yyyy = 0;
            this.mm = 0;
            this.dd = 0;
            return;
        }
        if (this.ordinal > MAX_ORDINAL) {
            throw new IllegalArgumentException("invalid ordinal date: " + this.ordinal);
        }

        if (this.ordinal >= GC_firstOrdinal) {
            this.yyyy = (Leap400RuleYYYY + flooredMulDiv(this.ordinal - Jan_01_Leap400RuleYear, 10000, 3652425));
        } else if (this.ordinal >= Jan_01_0001) {
            this.yyyy = (4 + flooredMulDiv(this.ordinal - Jan_01_0004, 100, 36525));
        } else if (this.ordinal >= MIN_ORDINAL) {
            this.yyyy = (-1 + flooredMulDiv(this.ordinal - Jan_01_0001BC, 100, 36525));
        } else {
            throw new IllegalArgumentException("invalid ordinal date: " + this.ordinal);
        }

        int aim = this.ordinal + 1;

        if ((GC_firstOrdinal <= this.ordinal) && (this.ordinal <= GC_firstDec_31)) {
            aim += missingDays;
        }

        int ddd = aim - jan01OfYear(this.yyyy);
        while (ddd <= 0) {
            this.yyyy -= 1;
            ddd = aim - jan01OfYear(this.yyyy);
        }
        boolean leap = isLeap(this.yyyy);
        while (ddd > (leap ? 366 : 365)) {
            this.yyyy += 1;
            ddd = aim - jan01OfYear(this.yyyy);
            leap = isLeap(this.yyyy);
        }

        this.mm = dddToMM(ddd, leap);
        this.dd = (ddd - daysInYearPriorToMonth(this.mm, leap));
    }

    protected final void toOrdinal() {
        this.ordinal = toOrdinal(this.yyyy, this.mm, this.dd);
    }

    private void writeObject(ObjectOutputStream s)
            throws ClassNotFoundException, IOException {
        s.defaultWriteObject();
    }

    static {
        NORMALIZE = 2;
        OJC_lastYYYY = 1752;
        OJC_lastMM = 9;
        OJC_lastDD = 2;
        GC_firstYYYY = 1752;
        GC_firstMM = 9;
        GC_firstDD = 14;
        Leap100RuleYYYY = (GC_firstYYYY + 99) / 100 * 100;
        Leap400RuleYYYY = (GC_firstYYYY + 399) / 400 * 400;
        missingDays = GC_firstDD - OJC_lastDD - 1;
        AD_epochAdjustment = -719529;
        BC_epochAdjustment = AD_epochAdjustment + 365;
        MIN_ORDINAL = toOrdinal(-999999, 1, 1);
        Jan_01_0001BC = toOrdinal(-1, 1, 1);
        Jan_01_0001 = toOrdinal(1, 1, 1);
        Jan_01_0004 = toOrdinal(4, 1, 1);
        GC_firstOrdinal = toOrdinal(GC_firstYYYY, GC_firstMM, GC_firstDD);

        GC_firstDec_31 = toOrdinal(GC_firstYYYY, 12, 31);
        Jan_01_Leap100RuleYear = toOrdinal(Leap100RuleYYYY, 1, 1);
        Jan_01_Leap400RuleYear = toOrdinal(Leap400RuleYYYY, 1, 1);
        MAX_ORDINAL = toOrdinal(999999, 12, 31);

        usual_dddToMMTable = new int[365];
        int dddi = 0;
        for (int mmi = 1; mmi <= 12; mmi++) {
            int last = daysInMonth(mmi, false);
            for (int ddi = 0; ddi < last; ddi++) {
                usual_dddToMMTable[(dddi++)] = mmi;
            }

        }

        leap_dddToMMTable = new int[366];
        dddi = 0;
        for (int mmi = 1; mmi <= 12; mmi++) {
            int last = daysInMonth(mmi, true);
            for (int ddi = 0; ddi < last; ddi++) {
                leap_dddToMMTable[(dddi++)] = mmi;
            }
        }
    }
}