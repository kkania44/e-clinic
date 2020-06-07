package pl.clinic.project;

import pl.clinic.project.model.Appointment;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.*;

public class AvailableDate {

    private static final List<String> WORKING_HOURS = Arrays.asList(
            "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30");

    public static List<String> getWorkingDaysOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_MONTH);

        int maxDayNr = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<String> workingDays = new ArrayList<>();

        for (int i = today; i <= maxDayNr; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            Date date = calendar.getTime();
            final LocalDate localDate = date.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            String dateAsString = format.format(calendar.getTime());
            DayOfWeek dayOfWeek = DayOfWeek.of(localDate.get(ChronoField.DAY_OF_WEEK));
            if (!dayOfWeek.equals(DayOfWeek.SATURDAY) && !dayOfWeek.equals(DayOfWeek.SUNDAY)) {
                workingDays.add(dateAsString);
            }
        }
        return workingDays;
    }

    public static List<String> getAvailableHours(List<LocalTime> hours) {
        for (LocalTime time: hours) {
            String timeAsString = time.toString().substring(0,5);
            WORKING_HOURS.remove(timeAsString);
        }
        return WORKING_HOURS;
    }

}
