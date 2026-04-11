package utils.last_run_tracker;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LastRunTracker {

    private static final String LAST_RUN_DIR =
            System.getProperty("user.dir")
                    + "/src/test/resources/testDataFiles/";


    static {
        File dir = new File(LAST_RUN_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static boolean shouldRun(String user, String useCase) {
        try {
            String filePath = buildFilePath(user, useCase);
            File file = new File(filePath);

            if (!file.exists()) {
                return true; // first time for this user + use case
            }

            String lastRun = new String(Files.readAllBytes(file.toPath())).trim();
            LocalDate lastRunDate = LocalDate.parse(lastRun);
            LocalDate today = LocalDate.now();

            return ChronoUnit.DAYS.between(lastRunDate, today) >= 3;

        } catch (Exception e) {
            return true;
        }
    }

    public static void update(String user, String useCase) {
        try {
            String filePath = buildFilePath(user, useCase);
            Files.write(Paths.get(filePath),
                    LocalDate.now().toString().getBytes());
        } catch (Exception ignored) {}
    }

    private static String buildFilePath(String user, String useCase) {
        String u = user.replace(" ", "_");
        String c = useCase.replace(" ", "_");
        return LAST_RUN_DIR + "last_run_date_" + u + "_" + c + ".txt";
    }
}
