package br.com.uft.scicumulus.vs.database;

import java.awt.Color;

public final class StringTools {

    private static final boolean DEBUGGING = false;

    public static void beep() {
        System.out.print("\007");
        System.out.flush();
    }

    public static String canonical(String s) {
        if (s == null) {
            return "";
        }

        return s.trim();
    }

    public static String condense(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.indexOf("  ") < 0) {
            return s;
        }
        int len = s.length();

        StringBuffer b = new StringBuffer(len - 1);
        boolean suppressSpaces = false;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == ' ') {
                if (!suppressSpaces) {
                    b.append(c);
                    suppressSpaces = true;
                }

            } else {
                b.append(c);
                suppressSpaces = false;
            }
        }
        return b.toString();
    }

    public static int countLeading(String text, char c) {
        int count = 0;
        while ((count < text.length()) && (text.charAt(count) == c)) {
            count++;
        }

        return count;
    }

    public static int countTrailing(String text, char c) {
        int length = text.length();

        int count = 0;
        while ((count < length) && (text.charAt(length - 1 - count) == c)) {
            count++;
        }

        return count;
    }

    public static boolean isEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    public static boolean isLegal(String candidate, String legalChars) {
        for (int i = 0; i < candidate.length(); i++) {
            if (legalChars.indexOf(candidate.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isUnaccentedLowerCase(char c) {
        return ('a' <= c) && (c <= 'z');
    }

    public static boolean isUnaccentedUpperCase(char c) {
        return ('A' <= c) && (c <= 'Z');
    }

    public static String leftPad(String s, int newLen, boolean chop) {
        int grow = newLen - s.length();
        if (grow <= 0) {
            if (chop) {
                return s.substring(0, newLen);
            }

            return s;
        }

        if (grow <= 30) {
            return "                              ".substring(0, grow) + s;
        }

        return rep(' ', grow) + s;
    }

    public static long parseDirtyLong(String numStr) {
        numStr = numStr.trim();

        StringBuffer b = new StringBuffer(numStr.length());
        boolean negative = false;
        for (int i = 0; i < numStr.length(); i++) {
            char c = numStr.charAt(i);
            if (c == '-') {
                negative = true;
            } else if (('0' <= c) && (c <= '9')) {
                b.append(c);
            }
        }
        numStr = b.toString();
        if (numStr.length() == 0) {
            return 0L;
        }
        long num = Long.parseLong(numStr);
        if (negative) {
            num = -num;
        }
        return num;
    }

    public static long parseLongPennies(String numStr) {
        numStr = numStr.trim();

        StringBuffer b = new StringBuffer(numStr.length());
        boolean negative = false;
        int decpl = -1;
        for (int i = 0; i < numStr.length(); i++) {
            char c = numStr.charAt(i);
            switch (c) {
                case '-':
                    negative = true;
                    break;
                case '.':
                    if (decpl == -1) {
                        decpl = 0;
                    } else {
                        throw new NumberFormatException("more than one decimal point");
                    }

                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (decpl != -1) {
                        decpl++;
                    }
                    b.append(c);
                case '/':
            }

        }

        if (numStr.length() != b.length()) {
            numStr = b.toString();
        }

        if (numStr.length() == 0) {
            return 0L;
        }
        long num = Long.parseLong(numStr);
        if ((decpl == -1) || (decpl == 0)) {
            num *= 100L;
        } else if (decpl != 2) {
            throw new NumberFormatException("wrong number of decimal places.");
        }

        if (negative) {
            num = -num;
        }
        return num;
    }

    public static String penniesToString(long pennies) {
        boolean negative;
        if (pennies < 0L) {
            pennies = -pennies;
            negative = true;
        } else {
            negative = false;
        }
        String s = Long.toString(pennies);
        int len = s.length();
        switch (len) {
            case 1:
                s = "0.0" + s;
                break;
            case 2:
                s = "0." + s;
                break;
            default:
                s = s.substring(0, len - 2) + "." + s.substring(len - 2, len);
        }

        if (negative) {
            s = "-" + s;
        }
        return s;
    }

    public static int pluck(String s) {
        int result = 0;
        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }

        return result;
    }

    public static String quoteSQL(String sql) {
        StringBuffer sb = new StringBuffer(sql.length() + 5);
        sb.append('\'');
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '\'') {
                sb.append("''");
            } else {
                sb.append(c);
            }
        }
        sb.append('\'');
        return sb.toString();
    }

    public static String removeHead(String s, String head) {
        if ((s != null) && (s.startsWith(head))) {
            return s.substring(head.length());
        }

        return s;
    }

    public static String removeTail(String s, String tail) {
        if ((s != null) && (s.endsWith(tail))) {
            return s.substring(0, s.length() - tail.length());
        }

        return s;
    }

    public static String rep(char c, int count) {
        char[] s = new char[count];
        for (int i = 0; i < count; i++) {
            s[i] = c;
        }
        return new String(s).intern();
    }

    public static String rightPad(String s, int newLen, boolean chop) {
        int grow = newLen - s.length();
        if (grow <= 0) {
            if (chop) {
                return s.substring(0, newLen);
            }

            return s;
        }

        if (grow <= 30) {
            return s + "                              ".substring(0, grow);
        }

        return s + rep(' ', grow);
    }

    public static String squish(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        if (s.indexOf(' ') < 0) {
            return s;
        }
        int len = s.length();

        StringBuffer b = new StringBuffer(len - 1);
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c != ' ') {
                b.append(c);
            }
        }
        return b.toString();
    }

    public static String toBookTitleCase(String s) {
        char[] ca = s.toCharArray();

        boolean changed = false;
        boolean capitalise = true;
        boolean firstCap = true;
        for (int i = 0; i < ca.length; i++) {
            char oldLetter = ca[i];
            if ((oldLetter <= '/') || ((':' <= oldLetter) && (oldLetter <= '?')) || ((']' <= oldLetter) && (oldLetter <= '`'))) {
                capitalise = true;
            } else {
                if ((capitalise) && (!firstCap)) {
                    capitalise = (!s.substring(i, Math.min(i + 4, s.length())).equalsIgnoreCase("the ")) && (!s.substring(i, Math.min(i + 3, s.length())).equalsIgnoreCase("of ")) && (!s.substring(i, Math.min(i + 3, s.length())).equalsIgnoreCase("to "));
                }

                char newLetter = capitalise ? Character.toUpperCase(oldLetter) : Character.toLowerCase(oldLetter);

                ca[i] = newLetter;
                changed |= newLetter != oldLetter;
                capitalise = false;
                firstCap = false;
            }
        }
        if (changed) {
            s = new String(ca);
        }
        return s;
    }

    public static String toHexString(int h) {
        String s = Integer.toHexString(h);
        if (s.length() < 8) {
            s = "00000000".substring(0, 8 - s.length()) + s;
        }
        return "0x" + s;
    }

    public static String toLZ(int i, int len) {
        String s = Integer.toString(i);
        if (s.length() > len) {
            return s.substring(s.length() - len);
        }
        if (s.length() < len) {
            return "000000000000000000000000000000".substring(0, len - s.length()) + s;
        }

        return s;
    }

    public static String toString(Color c) {
        String s = Integer.toHexString(c.getRGB() & 0xFFFFFF);
        if (s.length() < 6) {
            s = "000000".substring(0, 6 - s.length()) + s;
        }
        return '#' + s;
    }

    public static String trimLeading(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        int st = 0;
        while ((st < len) && (s.charAt(st) <= ' ')) {
            st++;
        }
        return st > 0 ? s.substring(st, len) : s;
    }

    public static String trimLeading(String text, char c) {
        int count = countLeading(text, c);

        return text.substring(count);
    }

    public static String trimTrailing(String s) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        int origLen = len;
        while ((len > 0) && (s.charAt(len - 1) <= ' ')) {
            len--;
        }
        return len != origLen ? s.substring(0, len) : s;
    }

    public static String trimTrailing(String text, char c) {
        int count = countTrailing(text, c);

        return text.substring(0, text.length() - count);
    }

    static char toLowerCase(char c) {
        return ('A' <= c) && (c <= 'Z') ? (char) (c + ' ') : c;
    }

    static String toLowerCase(String s) {
        char[] ca = s.toCharArray();
        int length = ca.length;
        boolean changed = false;

        for (int i = 0; i < length; i++) {
            char c = ca[i];
            if (('A' <= c) && (c <= 'Z')) {
                ca[i] = ((char) (c + ' '));
                changed = true;
            }
        }

        return changed ? new String(ca) : s;
    }

    public static void _main(String[] args) {
    }
}