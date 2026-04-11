package utilities.device_data_Reader;

// This class represents the data for a device (identity + Appium / UiAutomator2 options from devices.json).
public class DeviceData {
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public static class Device {
        private String firstName;
        private String automationName;
        private String platformName;
        private String platformVersion;
        private String deviceName;
        private String udid;

        /** App path; if blank, DesiredCapabilitiesManager resolves APK from env (local/CI). */
        private String app;

        private Boolean autoGrantPermissions;
        private Boolean noReset;
        private Boolean fullReset;
        private Boolean appWaitForLaunch;

        private Integer newCommandTimeoutSeconds;
        private Integer uiautomator2ServerInstallTimeout;
        private Integer uiautomator2ServerLaunchTimeout;
        private Integer adbExecTimeout;

        private Boolean ignoreHiddenApiPolicyError;
        private Boolean instrumentationKeepAlive;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getAutomationName() {
            return automationName;
        }

        public void setAutomationName(String automationName) {
            this.automationName = automationName;
        }

        public String getPlatformName() {
            return platformName;
        }

        public void setPlatformName(String platformName) {
            this.platformName = platformName;
        }

        public String getPlatformVersion() {
            return platformVersion;
        }

        public void setPlatformVersion(String platformVersion) {
            this.platformVersion = platformVersion;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public String getUdid() {
            return udid;
        }

        public void setUdid(String udid) {
            this.udid = udid;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public Boolean getAutoGrantPermissions() {
            return autoGrantPermissions;
        }

        public void setAutoGrantPermissions(Boolean autoGrantPermissions) {
            this.autoGrantPermissions = autoGrantPermissions;
        }

        public Boolean getNoReset() {
            return noReset;
        }

        public void setNoReset(Boolean noReset) {
            this.noReset = noReset;
        }

        public Boolean getFullReset() {
            return fullReset;
        }

        public void setFullReset(Boolean fullReset) {
            this.fullReset = fullReset;
        }

        public Boolean getAppWaitForLaunch() {
            return appWaitForLaunch;
        }

        public void setAppWaitForLaunch(Boolean appWaitForLaunch) {
            this.appWaitForLaunch = appWaitForLaunch;
        }

        public Integer getNewCommandTimeoutSeconds() {
            return newCommandTimeoutSeconds;
        }

        public void setNewCommandTimeoutSeconds(Integer newCommandTimeoutSeconds) {
            this.newCommandTimeoutSeconds = newCommandTimeoutSeconds;
        }

        public Integer getUiautomator2ServerInstallTimeout() {
            return uiautomator2ServerInstallTimeout;
        }

        public void setUiautomator2ServerInstallTimeout(Integer uiautomator2ServerInstallTimeout) {
            this.uiautomator2ServerInstallTimeout = uiautomator2ServerInstallTimeout;
        }

        public Integer getUiautomator2ServerLaunchTimeout() {
            return uiautomator2ServerLaunchTimeout;
        }

        public void setUiautomator2ServerLaunchTimeout(Integer uiautomator2ServerLaunchTimeout) {
            this.uiautomator2ServerLaunchTimeout = uiautomator2ServerLaunchTimeout;
        }

        public Integer getAdbExecTimeout() {
            return adbExecTimeout;
        }

        public void setAdbExecTimeout(Integer adbExecTimeout) {
            this.adbExecTimeout = adbExecTimeout;
        }

        public Boolean getIgnoreHiddenApiPolicyError() {
            return ignoreHiddenApiPolicyError;
        }

        public void setIgnoreHiddenApiPolicyError(Boolean ignoreHiddenApiPolicyError) {
            this.ignoreHiddenApiPolicyError = ignoreHiddenApiPolicyError;
        }

        public Boolean getInstrumentationKeepAlive() {
            return instrumentationKeepAlive;
        }

        public void setInstrumentationKeepAlive(Boolean instrumentationKeepAlive) {
            this.instrumentationKeepAlive = instrumentationKeepAlive;
        }
    }

}
