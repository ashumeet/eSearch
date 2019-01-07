package com.elasticsearch.data.format;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class createSetupAndDataFile {
	public static void main(String[] args) throws Exception{
		
		String inputTxtFileName = "/Users/ashu/Documents/Workspace/eSearch/src/main/esSetup/f_5500_2017_latest_layout.txt";
		String inputCsvFileName = "/Users/ashu/Documents/Workspace/eSearch/src/main/esSetup/f_5500_2017_latest.csv";

		String outputSetupFileName = "/Users/ashu/Documents/Workspace/eSearch/src/main/esSetup/setup/setup.json";
		String outputBulkFileName = "/Users/ashu/Documents/Workspace/eSearch/src/main/esSetup/data/search_bulk";
		
		
		StringBuffer sb = new StringBuffer();
		HashMap<String, Boolean> number = new HashMap<>();
		sb.append("{  \"settings\": { \"number_of_shards\": 1, \"number_of_replicas\": 0 }, \"mappings\": { \"search\": { \"properties\": {");
		try (Stream<String> stream = Files.lines(Paths.get(inputTxtFileName))) {
	        stream.forEach(line -> {
	        	String[] breaked = line.split(",");
	        	if(breaked.length == 4 && breaked[2].equals("TEXT")) {
	        		sb.append("\"" + breaked[1] + "\": { \"type\": \"text\"},");
	        	} else if(breaked.length == 3 && breaked[2].equals("NUMERIC")) {
	        		sb.append("\"" + breaked[1] + "\": { \"type\": \"integer\", \"null_value\": 0 },");	
	        		number.put(breaked[2], true);
	        	}
	        });
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("}}}}");
		
		File setup = new File(outputSetupFileName);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(setup))) {
		    writer.write(sb.toString());
		}

		StringBuffer sb1 = new StringBuffer();
		AtomicInteger fileNumber = new AtomicInteger(0);
		try (Stream<String> stream = Files.lines(Paths.get(inputCsvFileName))) {
			List<String> headerData = new ArrayList<>();
			AtomicInteger counter = new AtomicInteger(0);
			AtomicInteger id = new AtomicInteger(0);
	        stream.forEach(line -> {
	        	String[] data = line.split(",");
	        	if(data[0].startsWith("\"")) {
	        		sb1.append("{ \"index\" : { \"_index\" : \"csv_doc\", \"_type\" : \"search\", \"_id\" : \"" + id.incrementAndGet() + "\" } }");
	        		sb1.append(System.getProperty("line.separator"));
	        		sb1.append("{ ");
		        	int i = 0;
	        		for (String head : headerData) {
	        			if(data.length > i) {
		        			String fieldData = data[i];
	        				if(fieldData.length() > 2 && fieldData.startsWith("\"")) {
			        			 fieldData = fieldData.substring(1, fieldData.length()-2);
	        				} else if (fieldData.length()==2) {
	        					i++;
	        					continue;
	        				}
	        				if(fieldData.length() == 0) {
	        					i++;
	        					continue;	        					
	        				}
	        				if (number.containsKey(head)) {
				        		sb1.append("\"").append(head).append("\"").append(": ").append(fieldData).append(", ");
	        				} else {
				        		sb1.append("\"").append(head).append("\"").append(": ").append("\"").append(fieldData.replace("\"", "\\\"").replace("}","\\}")).append("\"").append(", ");
	        				}
	        			}
						i++;
					}
	        		sb1.deleteCharAt(sb1.length()-2);
	        		sb1.append("}");
	        		sb1.append(System.getProperty("line.separator"));
	        	} else {
	        		headerData.addAll(Arrays.asList(data));
	        	}
	        	
	        	if(counter.incrementAndGet()>2000) {
		        	File bulk = new File(outputBulkFileName + fileNumber.incrementAndGet() + ".json");
		    		try (BufferedWriter writer = new BufferedWriter(new FileWriter(bulk))) {
		    		    writer.write(sb1.toString());
		    		} catch (IOException e) {
						e.printStackTrace();
		    		}
		        	counter.set(0);
		    		sb1.delete(0, sb1.length());
	        	}
	        });
		}
		File bulk = new File(outputBulkFileName + fileNumber.incrementAndGet() + ".json");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(bulk))) {
		    writer.write(sb1.toString());
		}
	}
}
