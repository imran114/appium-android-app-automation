package utils.editPropertyFile;

import lombok.Getter;

@Getter
public class ProfileDetails {
    public String number = "";
    public String name = "";
    public String dueDate = "";
    public String package_ = "";

    public ProfileDetails() {
    }
    public void setName(String name) {
        this.name = name;
    }

    public ProfileDetails(String number, String name, String dueDate, String package_) {
        this.number = number;
        this.name = name;
        this.dueDate = dueDate;
        this.package_ = package_;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setPackage(String package_) {
        this.package_ = package_;
    }
}
