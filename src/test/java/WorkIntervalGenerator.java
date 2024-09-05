import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class WorkIntervalGenerator {

    @Test
    public void methodTest() {
        String outputFilePath = "generated_work_intervals.json";
        try {
            // Call the method to generate the JSON file
            generateWorkIntervals(outputFilePath);

            // Check if the file was created
            File file = new File(outputFilePath);
            assertTrue(file.exists(), "The JSON file was not created.");

            // Optionally, you can parse the file and verify the structure
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(file);
            assertTrue(rootNode.isArray(), "The root of the JSON should be an array.");
            assertTrue(rootNode.size() > 0, "The JSON array should not be empty.");
            assertTrue(rootNode.get(0).has("userId"), "The first element should have a 'userId' field.");

            System.out.println("Test passed: JSON file created and validated successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            fail("An exception occurred: " + e.getMessage());
        }
    }

    public static void generateWorkIntervals(String outputFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode workIntervalsArray = mapper.createArrayNode();

        // Track which users have been assigned "CO" and "CM"
        Set<Integer> coAssignedUsers = new HashSet<>();
        Set<Integer> cmAssignedUsers = new HashSet<>();

        for (int userId = 2; userId <= 27; userId++) {
            addUserWorkIntervals(userId, workIntervalsArray, mapper, coAssignedUsers, cmAssignedUsers);
        }

        // Write the generated JSON array to a file
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputFilePath), workIntervalsArray);
        System.out.println("Work intervals generated and saved to " + outputFilePath);
    }

    private static void addUserWorkIntervals(int userId, ArrayNode workIntervalsArray, ObjectMapper mapper,
                                             Set<Integer> coAssignedUsers, Set<Integer> cmAssignedUsers) {
        LocalDate startDate = LocalDate.of(2024, 8, 1);
        LocalDate endDate = LocalDate.of(2024, 8, 31);
        LocalDate currentDate = startDate;

        Set<Integer> coDays = new HashSet<>();
        Set<Integer> cmDays = new HashSet<>();

        // Assign 5 CO days for 5 users and 3 CM days for 2 users
        if (coAssignedUsers.size() < 5) {
            assignRandomDays(coDays, 5, startDate, endDate);
            coAssignedUsers.add(userId);
        }
        if (cmAssignedUsers.size() < 2) {
            assignRandomDays(cmDays, 3, startDate, endDate);
            cmAssignedUsers.add(userId);
        }

        while (!currentDate.isAfter(endDate)) {
            if (!isWeekend(currentDate)) {
                ObjectNode interval = mapper.createObjectNode();
                interval.put("userId", userId);

                String timeOffType = getTimeOffType(currentDate, coDays, cmDays);

                if (timeOffType != null) {
                    // For CO or CM days, set fields to null and use the provided structure
                    interval.putNull("firstStartTime");
                    interval.put("breaks", 0);
                    interval.putNull("breaksTime");
                    interval.putNull("endTime");
                    interval.putNull("totalWorkedTime");
                    interval.put("timeOffType", timeOffType);
                    interval.putArray("workDate").add(2024).add(8).add(currentDate.getDayOfMonth());
                    interval.put("totalWorkedSeconds", 0);
                } else {
                    // Normal work program from 7:00 AM to 3:30 PM
                    LocalDateTime startTime = currentDate.atTime(7, 0);
                    LocalDateTime endTime = currentDate.atTime(15, 30);

                    interval.putArray("firstStartTime")
                            .add(startTime.getYear())
                            .add(startTime.getMonthValue())
                            .add(startTime.getDayOfMonth())
                            .add(startTime.getHour())
                            .add(startTime.getMinute())
                            .add(startTime.getSecond());

                    interval.put("breaks", 2);
                    interval.putArray("breaksTime").add(1).add(0);

                    interval.putArray("endTime")
                            .add(endTime.getYear())
                            .add(endTime.getMonthValue())
                            .add(endTime.getDayOfMonth())
                            .add(endTime.getHour())
                            .add(endTime.getMinute())
                            .add(endTime.getSecond());

                    interval.putArray("totalWorkedTime").add(8).add(30); // 8 hours and 30 minutes of work
                    interval.put("totalWorkedSeconds", (8 * 3600) + (30 * 60)); // total worked seconds
                    interval.put("timeOffType", (JsonNode) null);  // Normal working day
                    interval.putArray("workDate").add(2024).add(8).add(currentDate.getDayOfMonth());
                }

                workIntervalsArray.add(interval);
            }

            currentDate = currentDate.plusDays(1);
        }
    }

    private static String getTimeOffType(LocalDate currentDate, Set<Integer> coDays, Set<Integer> cmDays) {
        int dayOfMonth = currentDate.getDayOfMonth();
        if (coDays.contains(dayOfMonth)) {
            return "CO";
        } else if (cmDays.contains(dayOfMonth)) {
            return "CM";
        }
        return null;  // Normal working day
    }

    private static void assignRandomDays(Set<Integer> daysSet, int numDays, LocalDate startDate, LocalDate endDate) {
        Random random = new Random();
        while (daysSet.size() < numDays) {
            int randomDay = random.nextInt(endDate.getDayOfMonth() - startDate.getDayOfMonth() + 1) + startDate.getDayOfMonth();
            daysSet.add(randomDay);
        }
    }

    private static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() >= 6;  // Saturday and Sunday
    }
}
