package utils.editPropertyFile;//package utils.editPropertyFile;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.*;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.google.common.base.Charsets;
//import com.google.gson.Gson;
//import io.appium.java_client.AppiumDriver;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import utils.seleniumUtils.SeleniumUtils;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class EditProfileDetails extends SeleniumUtils {
//
//    private static String platform;
//    Properties properties;
//    FileInputStream fileInputStream;
//    String profiledetailsfilepath = "qa/src/test/resources/testDataFiles/translation/profile_details.json";
//    ProfileDetails profiledetails = new ProfileDetails();
//    String data = "";
//
//    public EditProfileDetails(AppiumDriver driver, String platform) {
//        super(driver, platform);
//        this.platform = platform;
//        loadProfileDetails();
//    }
//
//    private void loadProfileDetails() {
//        try {
//            String profiledetailsfilepath = "qa/src/test/resources/testDataFiles/translation/profile_details.json";
//
////            fileInputStream = platform.equalsIgnoreCase("iOS") ? new FileInputStream(iOSLocatorFile) : new FileInputStream(androidLocatorFile);
//            if (platform.equalsIgnoreCase("iOS")) {
//                fileInputStream = new FileInputStream("/Users/qa/IdeaProjects/PTCL Automation/qa/src/test/resources/testDataFiles/translation/profile_details.json");
//            } else {
//                fileInputStream = new FileInputStream(profiledetailsfilepath); //qa/src/main/resources/locators/androidLocator.properties
//            }
//            properties = new Properties();
//            properties.load(fileInputStream);
//            System.out.println(fileInputStream.toString());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public String viewCompleteProfileDetailsJson() {
//        File file = new File("qa/src/test/resources/testDataFiles/translation/profile_details.json");
//        data = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
//        return data;
//    }
//
//
//
//}
