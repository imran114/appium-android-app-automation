package utilities.email;


import com.google.gson.annotations.SerializedName;


public class EmailConfig {
    @SerializedName("smtp")
    private SmtpConfig smtp;

    @SerializedName("sender")
    private Sender sender;

    @SerializedName("recipients")
    private Recipients recipients;

    @SerializedName("subject")
    private String subject;

    @SerializedName("bodyTemplate")
    private String bodyTemplate;

    @SerializedName("bodyTemplateExtent")
    private String bodyTemplateExtent;

    public SmtpConfig getSmtp() {
        return smtp;
    }

    public void setSmtp(SmtpConfig smtp) {
        this.smtp = smtp;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Recipients getRecipients() {
        return recipients;
    }

    public void setRecipients(Recipients recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public String getBodyTemplateExtent() {
        return bodyTemplateExtent;
    }

    public void setBodyTemplateExtent(String bodyTemplateExtent) {
        this.bodyTemplateExtent = bodyTemplateExtent;
    }
}

