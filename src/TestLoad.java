import java.io.BufferedReader;
import java.io.FileReader;

public class TestLoad {
	
	public static void main(String[] args){
		
		try {
		
			BufferedReader json_reader = new BufferedReader(new FileReader("/home/mike/save.json"));

			String json_1 = json_reader.readLine();
			String json_2 = json_reader.readLine();
		
			Object[] arr_1 = JSONObject.JSONToArray(json_1);
			Object[] arr_2 = JSONObject.JSONToArray(json_2);
			
			json_reader.close();
		} catch(Exception x){
			x.printStackTrace();
		}
		
	}
	
}