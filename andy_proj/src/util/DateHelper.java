package util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// This is the only class that uses java.time. It checks dates and finds days left.
public class DateHelper {

    // Checks that a date is in yyyy-MM-dd format and is not before today.
    public static boolean isValidDueDate(String dateText) {
        try {
            LocalDate dueDate = LocalDate.parse(dateText);
            LocalDate today = LocalDate.now();
            return !dueDate.isBefore(today);
        } catch (Exception e) {
            return false;
        }
    }

    // Returns a friendly message about how many days are left.
    public static String getDaysLeftText(String dateText) {
        LocalDate dueDate = LocalDate.parse(dateText);
        long days = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

        if (days == 0) {
            return "Due today";
        } else if (days == 1) {
            return "1 day left";
        } else if (days > 1) {
            return days + " days left";
        } else if (days == -1) {
            return "Overdue by 1 day";
        } else {
            return "Overdue by " + (-days) + " days";
        }
    }
}
