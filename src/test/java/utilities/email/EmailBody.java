package utilities.email;
import utilities.file_reader.PropertiesFileReader;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class EmailBody {

    private PropertiesFileReader propertiesFileReader;
    public EmailBody() {
        propertiesFileReader = new PropertiesFileReader();
    }
    private static  String path = "/src/test/resources/testDataFiles/email_body.README";

    // qa/src/test/resources/testDataFiles/email_body.README


    private static final  String userDir = System.getProperty("user.dir");


    private static String filePath(){
        return userDir+path;
    }
    public void enterTextToEmailBody(String text){
        text = text+"\n";
        try {
            Files.write(Paths.get(PropertiesFileReader.returnFilePath(path)), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Fail, unable to enter text to email body due to "+e.getMessage());
        }
    }

    public Map<String, String> enterTextToEmailBody(String key, String value) {
        Map<String, String> keyValueMap = new HashMap<>();
        keyValueMap.put(key, value);

        StringBuilder textBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
            textBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        String text = textBuilder.toString();
        try {
            Files.write(Paths.get(PropertiesFileReader.returnFilePath(path)), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return keyValueMap;
    }
    public static void clearEmailBody(){
        File file = new File(PropertiesFileReader.returnFilePath(path));
        PrintWriter writer;
        try {
            writer = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        writer.print("");
        writer.close();
    }
}