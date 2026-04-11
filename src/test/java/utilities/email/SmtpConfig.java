package utilities.email;


import com.google.gson.annotations.SerializedName;

public class SmtpConfig {
    @SerializedName("host")
    private String host;

    @SerializedName("port")
    private String port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}

