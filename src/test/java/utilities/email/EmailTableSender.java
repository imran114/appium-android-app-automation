package utilities.email;

import com.google.gson.Gson;
import io.appium.java_client.AppiumDriver;
import utilities.reporter.ExtentReport;
import utilities.reporter.ReportUtils;
import utils.seleniumUtils.SeleniumUtils;

import javax.mail.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class EmailTableSender extends SeleniumUtils {

    private final ExtentReport extent;  // CHANGE: Remove = new ... (initialize in constructor)
    private final EmailConfig config;

    public EmailTableSender(AppiumDriver driver, ExtentReport testExtent) {
        super(driver);
        this.extent = testExtent;
        try {
            Gson gson = new Gson();
            config = gson.fromJson(new FileReader("src/test/resources/testDataFiles/email_config/email_config.json"), EmailConfig.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Email config file not found: src/test/resources/testDataFiles/email_config/email_config.json", e);
        }
    }

    public void sendEmail() {
        final String fromEmail = config.getSender().getEmail();
        final String password = config.getSender().getPassword();

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", config.getSmtp().getHost());
        mailProps.put("mail.smtp.port", config.getSmtp().getPort());
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.ssl.enable", "true");

        Session session = Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            javax.mail.internet.MimeMessage message = new javax.mail.internet.MimeMessage(session);
            message.setFrom(new javax.mail.internet.InternetAddress(fromEmail));

            // Add recipients
            Recipients recipients = config.getRecipients();
            for (String to : recipients.getTo()) {
                message.addRecipient(Message.RecipientType.TO, new javax.mail.internet.InternetAddress(to));
            }
            for (String cc : recipients.getCc()) {
                message.addRecipient(Message.RecipientType.CC, new javax.mail.internet.InternetAddress(cc));
            }

            // Subject
            String subject = config.getSubject() + " - " + new SimpleDateFormat("dd MMM yyyy").format(new Date());
            message.setSubject(subject);

            // Email body - MODIFIED: No template or summary; just the table with header
            String tableHtml = ReportUtils.getStyledSummaryTable(extent.getExtentReports());

            String bodyText = "<h3 style='font-family: Arial;'>Execution Summary</h3>" + tableHtml;

            Multipart multipart = new javax.mail.internet.MimeMultipart();
            javax.mail.internet.MimeBodyPart textPart = new javax.mail.internet.MimeBodyPart();
            textPart.setContent(bodyText, "text/html; charset=utf-8");
            multipart.addBodyPart(textPart);

            // Attachment
            String reportFile = getLatestReport();
            System.out.println("Report file: " + reportFile);  // ADD: For debugging
            if (reportFile != null) {
                javax.mail.internet.MimeBodyPart attachmentPart = new javax.mail.internet.MimeBodyPart();
                attachmentPart.attachFile(new File(reportFile));
                multipart.addBodyPart(attachmentPart);
            }

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("Email sent successfully with report attached.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLatestReport() {
        // CHANGE: Use your actual reports directory
        String reportsDirPath = "src/test/resources/reports";
        File reportsDir = new File(reportsDirPath);
        System.out.println("Scanning reports dir: " + reportsDir.getAbsolutePath());  // ADD: Debug log

        if (!reportsDir.exists() || !reportsDir.isDirectory()) {
            System.err.println("Reports directory not found: " + reportsDir.getAbsolutePath() +
                    " - Check if reports are generated in src/test/resources/reports/");
            // Fallback: Try relative to user.dir (project root)
            reportsDir = new File(System.getProperty("user.dir") + "/src/test/resources/reports");
            if (!reportsDir.exists()) {
                System.err.println("Fallback dir also not found. Ensure ExtentReport is configured to output here.");
                return null;
            }
            System.out.println("Using fallback dir: " + reportsDir.getAbsolutePath());
        }

        File[] files = reportsDir.listFiles((dir, name) ->
                name.endsWith(".html") && name.contains("ptcl")  // Optional: Filter for your naming pattern (e.g., "ptcl_3_October_2025.html")
        );

        if (files == null || files.length == 0) {
            System.err.println("No matching HTML reports found in " + reportsDir.getAbsolutePath() +
                    " - Expected something like 'ptcl_3_October_2025.html'");
            return null;
        }

        File latest = files[0];
        for (File file : files) {
            if (file.lastModified() > latest.lastModified()) {
                latest = file;
            }
        }
        System.out.println("Attaching latest report: " + latest.getAbsolutePath());  // ADD: Confirm attachment
        return latest.getAbsolutePath();
    }
}