package utilities.listener;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        System.out.println(" Starting test method: " + methodName);
    }


    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println(
                "Passed use case: " + result.getTestClass().getRealClass().getSimpleName() +
                        " | test case: "   + result.getMethod().getMethodName()
        );
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println(
                "Failed use case: " + result.getTestClass().getRealClass().getSimpleName() +
                        " | test case: "     + result.getMethod().getMethodName()
        );
    }


    @Override
    public void onTestSkipped(ITestResult result) {
        String clazz  = result.getTestClass().getRealClass().getSimpleName();
        String method = result.getMethod().getMethodName();
        System.out.println(
                " Skipped use case: " + clazz +
                        " | test case: "     + method
        );
    }


    @Override
    public void onFinish(ITestContext context) {

    }

}

