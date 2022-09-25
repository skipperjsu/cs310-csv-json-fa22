package edu.jsu.mcis.cs310;

import java.io.*;
import java.util.*;
import com.opencsv.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Converter {
    
    /*
        
        Consider the following CSV data:
        
        "ID","Total","Assignment 1","Assignment 2","Exam 1"
        "111278","611","146","128","337"
        "111352","867","227","228","412"
        "111373","461","96","90","275"
        "111305","835","220","217","398"
        "111399","898","226","229","443"
        "111160","454","77","125","252"
        "111276","579","130","111","338"
        "111241","973","236","237","500"
        
        The corresponding JSON data would be similar to the following (tabs and
        other whitespace have been added for clarity).  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data!
        
        {
            "colHeaders":["ID","Total","Assignment 1","Assignment 2","Exam 1"],
            "rowHeaders":["111278","111352","111373","111305","111399","111160",
            "111276","111241"],
            "data":[[611,146,128,337],
                    [867,227,228,412],
                    [461,96,90,275],
                    [835,220,217,398],
                    [898,226,229,443],
                    [454,77,125,252],
                    [579,130,111,338],
                    [973,236,237,500]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and JSON.simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String results = "";
        
        try {
            
            // Initialize CSV Reader and Iterator
            
            CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> full = reader.readAll();
            Iterator<String[]> iterator = full.iterator();
            
            /* INSERT YOUR CODE HERE */
            
            // Where the json string will be built
            JSONObject json = new JSONObject();
            // Container for when the row headers and data are being worked with
            String[] record;
            
            // Column headers
            // Movement from array to list for compatibility with json
            List<String> cHeadings = new ArrayList<>();
            String[] cHeader = iterator.next();
            for (int i = 0; i < cHeader.length; i++) {
                cHeadings.add(cHeader[i]);
            }
            json.put("colHeaders", cHeadings);
            
            // Row headers and data in the same while statement
            List<String> rHeadings = new ArrayList();
            List<List<Integer>> ints = new ArrayList();
            
            while (iterator.hasNext()) {
                ArrayList nextdata = new ArrayList();
                record = iterator.next();
                // Initialize new object here so the objects don't compound on each other
                for (int i = 0; i < cHeadings.size(); i++) {
                    if (i == 0) {
                        // Row heading handler
                        rHeadings.add(record[i]);
                    } else {
                        // Data handler (type must be changed to integer)
                        nextdata.add(Integer.parseInt(record[i]));
                    }
                }
                // Put given set of data into array
                ints.add(nextdata);
            }
            json.put("rowHeaders", rHeadings);
            json.put("data", ints);
            
            // Convert built object to a String
            results = JSONValue.toJSONString(json);
            
        } catch(Exception e) { e.printStackTrace(); }
        
        // Return JSON String
        
        return results.trim();
        
    }
    
    public static String jsonToCsv(String jsonString) {
        
        String results = "";
        
        try {
            
            // Initialize JSON Parser and CSV Writer
            
            JSONParser parser = new JSONParser();
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            
            /* INSERT YOUR CODE HERE */
            // Parse the json string and make it a JSONObject that can be worked with
            JSONObject json = (JSONObject)parser.parse(jsonString);
            
            // Handle column headers first as they are the first line of csv
            // Store in JSONArray to be able to pull elements into a normal list
            JSONArray colsArray = (JSONArray)json.get("colHeaders");
            // List where headers will be placed
            List<String> colsList = new ArrayList();
            for (int i = 0; i < colsArray.size(); i++) {
                // Each element is an Object that needs to be casted into a String
                colsList.add((String)colsArray.get(i));
            }
            
            // Convert list to array to make it usable by csvWriter
            String[] cols = new String[colsList.size()];
            cols = colsList.toArray(cols);
            csvWriter.writeNext(cols);
            
            // Row headers and data in tandem, same setup as columns
            JSONArray rowsArray = (JSONArray)json.get("rowHeaders");
            JSONArray dataArray = (JSONArray)json.get("data");
            // Only one list necessary because the string is written by row
            List<String> rowList = new ArrayList();
            
            for (int i = 0; i < rowsArray.size(); i++) {
                // Add row heading first
                rowList.add((String)rowsArray.get(i));
                // Convert data out of array into individual Strings, not numbers
                JSONArray row = (JSONArray)dataArray.get(i);
                // The data comes in long format, not int
                long data;
                for (int j = 0; j < row.size(); j++) {
                    data = (long)row.get(j);
                    rowList.add(Long.toString(data));
                }
                // Add a line to the StringWriter as was done above
                String[] rowArray = new String[rowList.size()];
                rowArray = rowList.toArray(rowArray);
                csvWriter.writeNext(rowArray);
                // Reset rowList, rowArray will reset itself
                rowList = new ArrayList();
            }
            results = writer.toString();
        }
        catch(Exception e) { e.printStackTrace(); }
        
        // Return CSV String
        
        return results.trim();
        
    }
	
}