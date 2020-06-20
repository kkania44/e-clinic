package pl.clinic.project;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;

public class AvailableDateTime {

    private List<String> WORKING_HOURS = Arrays.asList(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30");

    public static List<String> getWorkingDaysOfCurrentMonth(boolean tomorrow) {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_MONTH);
        int maxDayNr = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<String> workingDays = new ArrayList<>();

        for (int i = today; i <= maxDayNr; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            Date date = calendar.getTime();
            final LocalDate localDate = castDateToLocal(date);
            String dateAsString = getFormattedDateAsString(localDate);
            DayOfWeek dayOfWeek = getCurrentDayOfWeek(localDate);

            if (!dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY)) {
                workingDays.add(dateAsString);
            }
        }

        if (tomorrow) {
            workingDays.remove(0);
        }

        return workingDays;
    }

    public List<String> getWorkingHours() {
        return WORKING_HOURS;
    }

    public List<String> getAvailableHours(List<LocalTime> hours) {
        List<String> availableHours = new ArrayList<>(getWorkingHours());
        for (LocalTime time: hours) {
            String timeAsString = time.toString().substring(0,5);
            availableHours.remove(timeAsString);
        }
        return availableHours;
    }

    private static LocalDate castDateToLocal(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private static DayOfWeek getCurrentDayOfWeek(LocalDate localDate){
        return DayOfWeek.of(localDate.get(ChronoField.DAY_OF_WEEK));
    }

    private static String getFormattedDateAsString(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return formatter.format(localDate);
    }

}
