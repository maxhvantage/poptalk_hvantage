package in.lockerapplication.utility;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Simon on 7/3/2015.
 */
public class AdmarvelUserParamHelper extends UserDataPassingHelper {
    public AdmarvelUserParamHelper(Context context) {
        super(context);
        addUserParamKey("gender");
        addUserParamKey("msisdn");
        addUserParamKey("dob");
    }

    @Override
    protected Object formatUserParams(HashMap<String, Object> params) {
        HashMap<String,Object> formattedParams = new HashMap<String,Object>();
        for(String key : params.keySet()){
            formattedParams.put(formatKey(key),formatValue(params.get(key)));
        }
        return formattedParams;
    }

    @Override
    public Map<String,Object> fetchUserParams() {
        return (Map<String,Object>) super.fetchUserParams();
    }

    @Override
    protected String formatKey(String key){
        return key.toUpperCase();
    }
    @Override
    protected String formatValue(Object value){
        return (String) value;
    }

}
