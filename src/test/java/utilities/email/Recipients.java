package utilities.email;


import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Recipients {
    @SerializedName("to")
    private List<String> to;

    @SerializedName("cc")
    private List<String> cc;

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }
}

