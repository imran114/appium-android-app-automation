package utilities.test_validator;

import org.testng.Assert;
import org.testng.SkipException;

public class TestValidator {

    public static String validateTest(String actualResult) {
        if (actualResult.contains("Pass")) {
            System.out.println("✅ Test Passed");
            Assert.assertTrue(true); // Marks the test as Passed
            return "Test Passed Successfully";
        } else if (actualResult.contains("Fail")) {
            System.out.println("❌ Test Failed");
            Assert.fail("Test Failed with result: " + actualResult); // Marks the test as Failed
            return "Test Failed";
        } else if (actualResult.contains("Skip")) {
            String skipMessage = "⏭️ Test Skipped: Reason - " + actualResult;
            System.out.println(skipMessage);
            throw new SkipException(skipMessage); // Marks the test as Skipped
        } else {
            String unknownMessage = "⚠️ Unknown result status: " + actualResult;
            System.out.println(unknownMessage);
            Assert.fail("Unexpected test result: " + actualResult); // Marks the test as Failed
            return unknownMessage;
        }
    }

}
