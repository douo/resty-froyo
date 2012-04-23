package us.monoid.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;
import java.util.List;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.json.JSONArray;

import com.smokejumperit.json.parser.JSONParser;
import com.smokejumperit.json.parser.ParseException;


/** A resource presentation in JSON format.
 * You can  ask Resty to parse the JSON into a JSONArray or a JSONObject. 
 * You can also access the JSON with a JSONPathQuery to extract only the parts you are interested in.
 * <p />
 * @author beders
 * @author RobertFischer
 */
public class JSONResource extends AbstractResource {

	Object json;
	
	public JSONResource(Option... options) {
		super(options);
	}

	/**
	* Parse and return JSON array. Parsing is done only once after which the inputStream is at EOF.
	*/
	public JSONArray array() throws IOException, JSONException {
		if(json == null) {
			try {
				List<?> ary = new JSONParser(inputStream).parseArray();
				json = ary;
			} catch(ParseException pe) {
				throw new JSONException(pe);
			} finally {
				inputStream.close();
			}
		}
		try {
			return new JSONArray((List<Object>)json);
		} catch(ClassCastException cce) {
			throw new IllegalStateException("Parsed JSON is not an array: " + toString(), cce);
		}
	}

	/** 
	 * Parse and return JSON object. Parsing is done only once after which the inputStrem is at EOF.
	 * @return the JSON object
	 * @throws IOException
	 * @throws JSONException
	 */
	public JSONObject object() throws IOException, JSONException {
		if (json == null) {
			try {
				Map<?,?> map = new JSONParser(inputStream).parseObject();
				json = map;
			} catch(ParseException pe) {
				throw new JSONException(pe);
			}finally {
				inputStream.close();
			}
		}
		try {
			return new JSONObject((Map<String,Object>)json);
		} catch(ClassCastException cce) {
			throw new IllegalStateException("Parsed JSON is not an object: " + toString(), cce);
		}
	}
	
	/** Added for compatibility with Scala. See Issue #2 at github.
	 * 
	 * @return the JSONObject presentation
	 * @throws IOException 
	 * @throws JSONException if data was not valid JSON
	 */
	public JSONObject toObject() throws IOException, JSONException {
		return object();
	}
	

	/** Execute the given path query on the json GET the returned URI expecting JSON
	 * 
	 * @param path path to the URI to follow
	 * @return a new resource, as a result of getting it from the server in JSON format
	 * @throws Exception 
	 * @throws JSONException 
	 */
	public JSONResource json(JSONPathQuery path) throws Exception {
		Object jsonValue = path.eval(this);
		return json(jsonValue.toString());
	}
	
	/** Execute the given path query on the json and POST to the returned URI expecting JSON
	 * 
	 * @param path path to the URI to follow
	 * @return a new resource, as a result of getting it from the server in JSON format
	 * @throws Exception 
	 * @throws JSONException 
	 */
	public JSONResource json(JSONPathQuery path, Content content) throws Exception {
		Object jsonValue = path.eval(this);
		
		return json(jsonValue.toString(), content);
	}

	/** Execute the given path query on the json and use the returned string as an URI expecting text/*
	 * 
	 * @param path path to the URI to follow
	 * @return a new resource, as a result of getting it from the server in text/plain format
	 * @throws Exception 
	 * @throws JSONException 
	 */
	public TextResource text(JSONPathQuery path) throws Exception {
		Object jsonValue = path.eval(this);
		return text(URI.create(jsonValue.toString()));
	}

	/** Execute the given path query on the json and GET the returned URI expecting text/*
	 * 
	 * @param path path to the URI to follow
	 * @return a new resource, as a result of getting it from the server in JSON format
	 * @throws Exception 
	 */
	public XMLResource xml(JSONPathQuery path) throws Exception {
		Object jsonValue = path.eval(this);
		return xml(jsonValue.toString());
	}
	
	/** Execute the given path query on the json and POST to the returned URI expecting text/*
	 * 
	 * @param path path to the URI to follow
	 * @return a new resource, as a result of getting it from the server in JSON format
	 * @throws Exception 
	 */
	public XMLResource xml(JSONPathQuery path, Content content) throws Exception {
		Object jsonValue = path.eval(this);
		return xml(jsonValue.toString(), content);
	}
	
	/** Gets the partial JSON object or attribute as specified in the path expression.*/
	public Object get(String path) throws Exception {
		return new JSONPathQuery(path).eval(this);
	}
	
	/** Gets the partial JSON object or attribute as specified in the path expression.*/
	public Object get(JSONPathQuery aQuery) throws Exception {
		return aQuery.eval(this);
	}

	@Override
	String getAcceptedTypes() {
		return "application/json";
	}

	@Override
	public String toString() {
		return json.toString();
	}

}
