package ctgraphdep.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.time.Month;
import java.time.Year;
import java.util.stream.IntStream;

public class DisplayUtil {

    public static void setupYearComboBox(ComboBox<Integer> yearComboBox) {
        int currentYear = Year.now().getValue();
        IntStream.rangeClosed(currentYear - 5, currentYear + 5)
                .boxed()
                .sorted((a, b) -> b.compareTo(a))
                .forEach(yearComboBox.getItems()::add);
        yearComboBox.setValue(currentYear);
    }

    public static void setupMonthComboBox(ComboBox<Month> monthComboBox) {
        monthComboBox.getItems().addAll(Month.values());
        monthComboBox.setValue(Month.of(java.time.LocalDate.now().getMonthValue()));
    }

    public static void updateSelectedUserLabel(Label selectedUserLabel, String userName, int employeeId) {
        selectedUserLabel.setText(String.format("Selected User: %s (Employee ID: %d)", userName, employeeId));
    }
}