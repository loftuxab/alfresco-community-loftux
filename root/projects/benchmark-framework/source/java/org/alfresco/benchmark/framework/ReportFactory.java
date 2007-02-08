/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.alfresco.benchmark.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.Ostermiller.util.CSVParser;
import com.sun.japex.TestCase;

/**
 * @author Roy Wetherall
 */
public class ReportFactory
{
    private static Map<String, Boolean> reportGenerated = new HashMap<String, Boolean>();
    
    public static void generate(String dataFile)
    {
        try
        {
            Map<String, BenchmarkTypeData> dataSummary = new HashMap<String, BenchmarkTypeData>();
            CSVParser parser = new CSVParser(new FileInputStream(dataFile));
            try
            {
                String[][] data = parser.getAllValues();
                boolean first = true;
                for (String[] line : data)
                {
                    if (first == true)
                    {
                        first = false;
                    }
                    else
                    {
                        // Get the data from this line
                        String benchmarkType = line[1];
                        double duration = Double.parseDouble(line[5]);
                        
                        BenchmarkTypeData benchmarkTypeData = dataSummary.get(benchmarkType);
                        if (benchmarkTypeData == null)
                        {
                            benchmarkTypeData = new BenchmarkTypeData(benchmarkType);
                            dataSummary.put(benchmarkType, benchmarkTypeData);
                        }
                        benchmarkTypeData.incrementCount();
                        benchmarkTypeData.incrementTotalDuration(duration);
                    }
                }
            }
            finally
            {
                parser.close();
            }
            
            // Generate the reports
            String outputFolder = getOutputFolder(dataFile);
            generateSummaryReport(outputFolder, dataSummary);
        }
        catch (Throwable exception)
        {
            exception.printStackTrace();
        }
    }
    
    public static synchronized void generate(TestCase testCase)
    {
        // Determine whether the report has already been genereated or not
        Boolean generated = reportGenerated.get(testCase.getName());
        if (generated == null || generated == Boolean.FALSE)
        {        
            try
            {
                String dataFile = BenchmarkUtils.getOutputFileLocation(testCase);
                
                // Generate reports
                generate(dataFile);                
                generateDetailsReport(getOutputFolder(dataFile), testCase);
                
                reportGenerated.put(testCase.getName(), Boolean.TRUE);
            }
            catch (Throwable exception)
            {
                exception.printStackTrace();
            }
        }
    }
    
    private static String getOutputFolder(String dataFile)
    {
        File file = new File(dataFile);
        String outputFolder = file.getPath().substring(0, file.getPath().length()-4);
        System.out.println(file.getPath());
        System.out.println(outputFolder);
        File outputFolderFile = new File(outputFolder);
        if (outputFolderFile.exists() == false)
        {
            outputFolderFile.mkdir();
        }
        return outputFolder;
    }
    
    private static void generateDetailsReport(String outputFolder, TestCase testCase)
        throws Exception
    {
        //Generate the output file
        File outputFile = new File(outputFolder + File.separator + "testCaseDetails.htm");
        FileOutputStream out = new FileOutputStream(outputFile);
        StringBuilder builder = new StringBuilder();
        try
        {
            builder.append("<html><head></head><body><h1>Details Report</h1><br>");
          
            // Output the general details of the test case                    
            builder.append("<table cellspacing='3' cellpadding=2' border='1'>");
            if (testCase.hasParam("alfresco.numberOfThreads") == true)
            {
                builder.append("<tr><td>Number of threads</td><td>");
                builder.append(testCase.getParam("alfresco.numberOfThreads"));
                builder.append("</td></tr>");
            }
            if (testCase.hasParam("alfresco.threadRampupTime") == true)
            {
                builder.append("<tr><td>Thread Rampup Time (Secs)</td><td>");
                builder.append(testCase.getParam("alfresco.threadRampupTime"));
                builder.append("</td></tr>");
            }
            if (testCase.hasParam("japex.runTime") == true)
            {
                builder.append("<tr><td>Run Time</td><td>");
                builder.append(testCase.getParam("japex.runTime"));
                builder.append("</td></tr>");
            }
            if (testCase.hasParam("japex.runIterations") == true)
            {
                builder.append("<tr><td>Run Iterations</td><td>");
                builder.append(testCase.getParam("japex.runIterations"));
                builder.append("</td></tr>");
            }
            if (testCase.hasParam("japex.runDelay") == true)
            {
                builder.append("<tr><td>Run Delay</td><td>");
                builder.append(testCase.getParam("japex.runDelay"));
                builder.append("</td></tr>");
            }
            if (testCase.hasParam("japex.runDelayVariation") == true)
            {
                builder.append("<tr><td>Run Delay Variation</td><td>");
                builder.append(testCase.getParam("japex.runDelayVariation"));
                builder.append("</td></tr>");
            }
            
            builder.append("</table></body></html>");
            
            out.write(builder.toString().getBytes());
        }
        finally
        {
            out.close();
        }
    }
    
    private static void generateSummaryReport(String outputFolder, Map<String, BenchmarkTypeData> dataSummary)
        throws Exception
    {
        // Generate the output file
        File outputFile = new File(outputFolder + File.separator + "summaryReport.htm");
        FileOutputStream out = new FileOutputStream(outputFile);
        StringBuilder builder = new StringBuilder();
        try
        {
            builder.append("<html><head></head><body><h1>Summary Report</h1><br>");                     
            
            builder.append("<table cellspacing='3' cellpadding=2' border='1'><tr><th>Test Type</th><th>Test Count</th><th>Total Duration (Sec)</th><th>Average</th><th>RPS</th></tr>");
            for (Map.Entry<String, BenchmarkTypeData> entry : dataSummary.entrySet())
            {
                int count = entry.getValue().getCount();
                double totalDuration = entry.getValue().getTotalDuration()/1000000000;
                double average = totalDuration/count;
                double rps = 1/average;
                
                builder.append("<tr><td>");
                builder.append(entry.getKey());
                builder.append("</td><td>");
                builder.append(count);
                builder.append("</td><td>");
                builder.append(Double.toString(totalDuration));
                builder.append("</td><td>");
                builder.append(Double.toString(average));
                builder.append("</td><td>");
                builder.append(Double.toString(rps));
                builder.append("</td></tr>");                        
            }
            builder.append("</table>");                    
            builder.append("</body></html>");
            
            out.write(builder.toString().getBytes());
        }
        finally
        {
            out.close();
        }
    }
}
