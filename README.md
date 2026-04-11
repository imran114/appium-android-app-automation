# Appium Android automation

Maven-based Java project for **Android** UI automation with **Appium 9**, **Selenium 4**, and **TestNG**. Page objects live under `pages`, shared UI helpers under `utils`, and test wiring under `testBase` / `utilities`.

**Artifact (Maven):** `org.example:seers-solutionz-task` · **JDK:** 17

---

## Tech stack

| Area | Libraries |
|------|-----------|
| Mobile driver | Appium Java Client 9.x, UiAutomator2 |
| Core automation | Selenium 4 |
| Tests & suite | TestNG 7 |
| Reporting | ExtentReports |
| Build | Maven |

---

## Repository layout

```
├── apps/                              # APK under test (default: apps/app.apk)
├── recordings/                        # Screen recordings (.mp4) — created at runtime (see below)
├── testNGSuite.xml                    # TestNG suite (listeners, parameters, classes)
├── pom.xml
├── src/main/java/
│   ├── locators/
│   │   └── AndroidLocatorRegistry.java    # Shared By constants for legacy/common UI helpers
│   ├── pages/
│   │   └── espresso/
│   │       ├── BasicEspressoSampleLocators.java   # Locators for the sample Espresso screen
│   │       └── BasicEspressoSamplePage.java       # Page methods (Pass/Fail strings)
│   └── utils/
│       ├── app_manager/               # App foreground / lifecycle helpers
│       ├── seleniumUtils/             # Waits, clicks, scroll, screenshots, etc.
│       ├── commonComponents/            # buttonActions, editTextFields, scrollMethods, …
│       ├── file_reader/ / fileWriter/ # Properties I/O
│       ├── json_reader/ / json_util/
│       ├── loader_utils/ / bounds/ / ad_handler/ / …
│       └── …
├── src/test/java/
│   ├── testBase/
│   │   ├── BaseClass.java             # Session lifecycle, TestNG params, Extent hook
│   │   ├── PageObjects.java           # @BeforeClass: app open, ButtonActions, recording
│   │   ├── DriverManager.java         # Builds AndroidDriver (default server URL in code)
│   │   └── DesiredCapabilitiesManager.java   # UiAutomator2 caps + devices.json
│   ├── tests/
│   │   └── espresso/
│   │       └── BasicEspressoSampleTest.java
│   └── utilities/
│       ├── device_data_Reader/        # devices.json → DeviceData
│       ├── find_apk/                  # Resolves apps/app.apk from project root
│       ├── listener/                  # SuiteListener, TestListener
│       ├── reporter/                  # ExtentReport, ReportUtils
│       ├── screen_recording_utils/
│       ├── server/                    # Appium server / port helpers (optional)
│       ├── email/                     # Optional report email (Windows path in BaseClass)
│       ├── file_reader/ / fileWriter/
│       └── test_validator/            # Pass/Fail/Skip assertion helper
└── src/test/resources/
    ├── testDataFiles/
    │   ├── devices.json               # Per-device identity + Appium options (matches suite udid)
    │   ├── espresso_sample.properties # Runtime: lastEnteredText (Espresso sample tests)
    │   └── login_credentials/         # Credentials JSON (if used by readers)
    ├── email_config/
    └── reports/                       # Extent HTML output (generated)
```

---

## Configuration

1. **APK** — Place the build at `apps/app.apk` (or set `"app"` in the matching `devices.json` entry to an absolute path). `FindAPKFiles` resolves the default relative path from the project root.

2. **`src/test/resources/testDataFiles/devices.json`** — One object per device; `udid` must match the TestNG parameter in `testNGSuite.xml`. Caps such as `platformVersion`, `automationName`, timeouts, and optional `app` override are read from here (`DesiredCapabilitiesManager` + `DeviceDataReader`).

3. **`testNGSuite.xml`** — Set `<parameter name="udid" …/>`, `<parameter name="env" …/>`, `<parameter name="loginID" …/>` (used for recording labels), and list test classes under `<classes>`.

4. **Appium** — Start a server (for example `http://127.0.0.1:4723/wd/hub` as used in `DriverManager`) before running tests, unless you change that URL.

---

## Run tests

```bash
mvn clean test
```

Or run the suite from the IDE using `testNGSuite.xml`. Extent reports are written under `src/test/resources/reports/` as `{apkBasename}_{d_MMMM_yyyy}.html` (for example `app_11_April_2026.html` when the APK is `apps/app.apk`; see `FindAPKFiles.PROJECT_APK_RELATIVE` and `ExtentReport`).

---

## Screen recordings

Tests that extend **`PageObjects`** start an Appium **screen recording** at `@BeforeClass` and stop it at `@AfterClass`, via **`ScreenRecordingUtils`**.

- **Where to find videos:** project root folder **`recordings/`** (created automatically if missing).
- **Layout:** files are saved under `recordings/{bucket}/`, where `bucket` is the TestNG **`loginID`** parameter (sanitized). If `loginID` is missing, the folder name is **`unknown`**. Each file is named `{TestClassName}_{yyyyMMdd_HHmmss_SSS}.mp4`.
- **Requirements:** a live Appium session and device support for `startRecordingScreen` / `stopRecordingScreen` (behavior can vary by Android version or OEM; see console `[REC]` logs if a run saves nothing).

---

## Adding tests

1. Add a **locator** class (or static inner locators) under `pages/<feature>/`.
2. Add a **page** class extending `SeleniumUtils`, reusing `ButtonActions` / `EditText` where useful; return `Pass, …` / `Fail, …` strings for `TestValidator`.
3. Add a **test** class under `src/test/java/tests/…` extending `PageObjects`, mirroring patterns in `BasicEspressoSampleTest`.
4. Register the class in `testNGSuite.xml`.

---

## License

See [LICENSE](LICENSE) in this repository.
