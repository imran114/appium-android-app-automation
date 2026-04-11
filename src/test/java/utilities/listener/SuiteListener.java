package utilities.listener;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.xml.XmlTest;

public class SuiteListener implements ISuiteListener {

    private boolean isServer(){
        String userName = System.getProperty("user.name", "");
        return userName.equalsIgnoreCase("qa")
                || userName.toLowerCase().contains("qa");
    }

    @Override
    public void onStart(ISuite suite) {
        System.out.println("Test Starting");
        // TODO comment these while pushing the code
        // Start the Appium server
//        if (!isServer()) AppiumServer.startAppiumServer();
//////
////         Get the details of the started Appium server
//        Map<String, String> serverDetails = AppiumServer.getAppiumServerDetails();
//        System.out.println(serverDetails.entrySet());
//        System.out.println("Appium Server Port: " + serverDetails.get("port"));
//        String serverPort = serverDetails.get("port").trim();

//        PropertiesFileWriter propertiesFileWriter = new PropertiesFileWriter();
//        propertiesFileWriter.updateAppiumPort(serverPort);
        fetchDeviceDetails(suite);

    }


    private void fetchDeviceDetails(ISuite suite) {
        try {
            // Fetch device details
//            String deviceName = DeviceManager.getDeviceName();
//            String platformVersion = DeviceManager.getPlatformVersion();
//            String udid = DeviceManager.getUDID();

//            System.out.println("udid: " + udid);
            // Debug logs
            System.out.println("Dynamically Setting Parameters:");
//            System.out.println("Device Name: " + deviceName);
//            System.out.println("Platform Version: " + platformVersion);
//            System.out.println("UDID: " + udid);

            // Set parameters dynamically for each XmlTest
            for (XmlTest test : suite.getXmlSuite().getTests()) {
//                test.addParameter("deviceName", deviceName);
//                test.addParameter("platformVersion", platformVersion);
//                test.addParameter("udid", udid);
                test.addParameter("language", "en");
            }
        } catch (Exception e) {
            e.getMessage();
            throw new RuntimeException("Failed to fetch and set device details.");
        }
    }


    @Override
    public void onFinish(ISuite suite) {
//        StartAppiumWithPlugin.stopAppiumServer();
    }

}
