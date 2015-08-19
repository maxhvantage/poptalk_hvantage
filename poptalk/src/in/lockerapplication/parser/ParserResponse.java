package in.lockerapplication.parser;

import in.lockerapplication.bean.CreditResponseBean;
import in.lockerapplication.networkKeys.NetworkKeys;

import org.json.JSONObject;

import com.ciao.app.utils.AppUtils;


public class ParserResponse {

	public CreditResponseBean parseCreditData(String response) {
		CreditResponseBean responseBean = new CreditResponseBean();
		try {

			JSONObject jsonObject = new JSONObject(response);
			int response_error_key = jsonObject.getInt(NetworkKeys.response_error_key.toString());
			if (response_error_key == 0){
				responseBean.setResponse_error_key(response_error_key);
				responseBean.setmResponseString(jsonObject.getString(NetworkKeys.response_string.toString()));
				JSONObject jResult=jsonObject.getJSONObject(NetworkKeys.result.toString());
				responseBean.setCredits(jResult.getString(NetworkKeys.total_credit.toString()));
				
			}else{
				responseBean.setResponse_error_key(response_error_key);
				responseBean.setmResponseString(jsonObject.getString(NetworkKeys.response_string.toString()));
			}
		} catch (Exception e){
			response = "ParsingException";
			responseBean.setmExceptionName(response);
			e.printStackTrace();
		}
		return responseBean;
	}
}
