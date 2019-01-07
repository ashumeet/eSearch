package com.amazonaws.lambda.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.json.simple.parser.ParseException;


public class LambdaFunctionHandler implements RequestStreamHandler {

	private JSONParser parser = new JSONParser();

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

		String searchHost = System.getenv("SEARCH_HOST");
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		HashMap<String, String> paramMap = new HashMap<>();
        String responseCode = "200";
        JSONObject responseJson = new JSONObject();
		JSONObject event = null;
		try {
			event = (JSONObject) parser.parse(reader);

			if (event.get("queryStringParameters") != null) {
				JSONObject qsp = (JSONObject) event.get("queryStringParameters");
				if (qsp.get("PLAN_NAME") != null)
						paramMap.put("PLAN_NAME", (String) qsp.get("PLAN_NAME"));
				if (qsp.get("SPONSOR_NAME") != null)
						paramMap.put("SPONSOR_DFE_NAME", (String) qsp.get("SPONSOR_NAME"));
				if (qsp.get("SPONSOR_STATE") != null)
						paramMap.put("SPONS_DFE_MAIL_US_STATE", (String) qsp.get("SPONSOR_STATE"));
			}

		Search search = new Search.Builder(createQuerry(paramMap)).addIndex("csv_doc").addType("search").build();
		
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder(searchHost).multiThreaded(true)
				.defaultMaxTotalConnectionPerRoute(2).maxTotalConnection(20).build());
		JestClient client = factory.getObject();

		SearchResult result = client.execute(search);
		
		JSONObject header = new JSONObject();
		responseJson.put("isBase64Encoded", true);
		responseJson.put("statusCode", 200);
		header.put("Content-Type", "application/json");
		responseJson.put("headers",header);		
		responseJson.put("body", result.getJsonString());

    } catch(ParseException pex) {
        responseJson.put("statusCode", "400");
        responseJson.put("exception", pex);
    }

    OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
    writer.write(responseJson.toJSONString());
    writer.close();
    
	}

	public String createQuerry(HashMap<String, String> match) {
		if (match.size() == 0) {
			return "{\"query\":{\"match_all\":{}}}";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("{ \"query\": { \"dis_max\": { \"queries\": [ ");
		for (String key : match.keySet()) {
			sb.append("{ \"match\": { \"" + key + "\": \"" + match.get(key) + "\" }},");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(" ]}}}");
		return sb.toString();
	}
}