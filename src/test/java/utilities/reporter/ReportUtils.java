package utilities.reporter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.model.Test;

import java.util.List;

public class ReportUtils {

    public static String getSummary(ExtentReports extent) {
        if (extent == null) {
            return "Passed Tests: 0\nFailed Tests: 0\nSkipped Tests: 0";
        }
        // UPDATED: Use leaf counts for summary too
        long passed = countLeafTests(extent.getReport().getTestList(), Status.PASS);
        long failed = countLeafTests(extent.getReport().getTestList(), Status.FAIL);
        long skipped = countLeafTests(extent.getReport().getTestList(), Status.SKIP);

        return "Passed Tests: " + passed + "\n"
                + "Failed Tests: " + failed + "\n"
                + "Skipped Tests: " + skipped;
    }

    public static String getStyledSummaryTable( ExtentReports extent) {
        List<Test> allTests = extent != null && extent.getReport() != null
                ? extent.getReport().getTestList() : java.util.Collections.emptyList();

        long totalPassed  = sumOverall(allTests, Status.PASS);
        long totalFailed  = sumOverall(allTests, Status.FAIL);
        long totalSkipped = sumOverall(allTests, Status.SKIP);

        // Always render our own table so numbers are correct
        return buildFallbackTablePerDeepest(allTests, totalPassed, totalFailed, totalSkipped);
    }

    // NEW: Recursive helper to count leaf tests (sub-tests/steps) matching a status
    private static long countLeafTests(List<Test> tests, Status status) {
        long count = 0;
        if (tests == null) return 0;

        for (Test t : tests) {
            List<Test> children = t.getChildren();  // ✅ correct method
            if (children == null || children.isEmpty()) {
                // leaf node
                Status s = t.getStatus();
                if (s != null && s == status) {
                    count++;
                }
            } else {
                // recurse on children
                count += countLeafTests(children, status);
            }
        }
        return count;
    }


    // UPDATED: Uses leaf counts for accurate "test cases"
    private static String buildFallbackTablePerDeepest(List<Test> allTests,
                                                       long totalPassed,
                                                       long totalFailed,
                                                       long totalSkipped) {

        long totalLeaves = totalPassed + totalFailed + totalSkipped;
        double overallPassPct = totalLeaves > 0 ? (double) totalPassed / totalLeaves * 100 : 0;

        StringBuilder tableHtml = new StringBuilder();
        tableHtml.append("<table style='border-collapse: collapse; width: 100%; font-family: Arial, sans-serif; font-size: 13px;'>");
        tableHtml.append("<thead><tr>");
        tableHtml.append("<th style='background-color: #2196F3; color: white; border: 1px solid #ddd; padding: 8px; text-align: center;'>Use Case</th>");
        tableHtml.append("<th style='background-color: #4CAF50; color: white; border: 1px solid #ddd; padding: 8px; text-align: center;'>Passed</th>");
        tableHtml.append("<th style='background-color: #F44336; color: white; border: 1px solid #ddd; padding: 8px; text-align: center;'>Failed</th>");
        tableHtml.append("<th style='background-color: #FF9800; color: white; border: 1px solid #ddd; padding: 8px; text-align: center;'>Skipped</th>");
        tableHtml.append("<th style='background-color: #9C27B0; color: white; border: 1px solid #ddd; padding: 8px; text-align: center;'>Passed %</th>");
        tableHtml.append("</tr></thead><tbody>");

        // Overall row
        tableHtml.append("<tr style='background-color: #e8f4fd; font-weight: bold;'>");
        tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: left;'>Overall</td>");
        tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center; color: green;'>" + totalPassed + "</td>");
        tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center; color: red;'>" + totalFailed + "</td>");
        tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center; color: orange;'>" + totalSkipped + "</td>");
        tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center; color: purple;'>" + String.format("%.1f", overallPassPct) + "%</td>");
        tableHtml.append("</tr>");

        // Per top-level “Use Case”: only deepest nodes under that test
        if (allTests != null && !allTests.isEmpty()) {
            for (int i = 0; i < allTests.size(); i++) {
                Test top = allTests.get(i);

                long p = hasChildTests(top)
                        ? countDeepestCasesByStatus(top.getChildren(), Status.PASS)
                        : (top.getStatus() == Status.PASS ? 1 : 0);

                long f = hasChildTests(top)
                        ? countDeepestCasesByStatus(top.getChildren(), Status.FAIL)
                        : (top.getStatus() == Status.FAIL ? 1 : 0);

                long s = hasChildTests(top)
                        ? countDeepestCasesByStatus(top.getChildren(), Status.SKIP)
                        : ((top.getStatus() == Status.SKIP || top.getStatus() == Status.WARNING) ? 1 : 0);

                long t = p + f + s;
                double pct = t > 0 ? (double)p / t * 100 : 0;

                tableHtml.append("<tr style='background-color: #f9f9f9;'>");
                tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: left;'>" + (i + 1) + ". " + top.getName() + "</td>");
                tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center; color: green;'>" + p + "</td>");
                tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center; color: red;'>" + f + "</td>");
                tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center; color: orange;'>" + s + "</td>");
                tableHtml.append("<td style='border: 1px solid #ddd; padding: 8px; text-align: center; color: purple;'>" + String.format("%.1f", pct) + "%</td>");
                tableHtml.append("</tr>");
            }
        } else {
            tableHtml.append("<tr><td colspan='5' style='border: 1px solid #ddd; padding: 8px; text-align: center; color: gray;'>No use cases executed</td></tr>");
        }

        tableHtml.append("</tbody></table>");
        return tableHtml.toString();
    }




    // Treat only deepest Test nodes as "test cases"
    private static boolean hasChildTests(Test t) {
        List<Test> kids = t.getChildren();
        return kids != null && !kids.isEmpty();
    }

    // Returns count of deepest nodes under `tests` matching `status`
// Maps WARNING -> SKIP defensively (some listeners use WARNING for “skipped”)
    private static long countDeepestCasesByStatus(List<Test> tests, Status status) {
        if (tests == null || tests.isEmpty()) return 0L;
        long count = 0L;
        for (Test t : tests) {
            List<Test> kids = t.getChildren();
            if (kids == null || kids.isEmpty()) {
                Status s = t.getStatus();
                if (s == null) continue;
                if (s == status) {
                    count++;
                } else if (status == Status.SKIP && s == Status.WARNING) {
                    // defensive: sometimes skips are recorded as WARNING
                    count++;
                }
            } else {
                count += countDeepestCasesByStatus(kids, status);
            }
        }
        return count;
    }

    // Convenience: sum across top-level tests (overall row)
    private static long sumOverall(List<Test> topLevel, Status status) {
        long total = 0L;
        for (Test t : topLevel) {
            total += countDeepestCasesByStatus(t.getChildren(), status);
            // Edge case: if a top-level test has no children at all, treat it as a single case
            if (!hasChildTests(t)) {
                Status s = t.getStatus();
                if (s != null && (s == status || (status == Status.SKIP && s == Status.WARNING))) {
                    total++;
                }
            }
        }
        return total;
    }

}