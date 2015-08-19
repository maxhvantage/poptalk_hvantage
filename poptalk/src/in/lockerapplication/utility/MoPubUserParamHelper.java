package in.lockerapplication.utility;

/**
 * Created by Simon on 7/3/2015.
 */

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 7/3/2015.
 */
public class MoPubUserParamHelper extends UserDataPassingHelper {
    public MoPubUserParamHelper(Context context) {
        super(context);
        addUserParamKey("gender");
        //addUserParamKey("countryCode");
        //addUserParamKey("dob");
    }
    /*
    * Mopub setKeywords method only accepts one String with params separated by commas, ANDs, or ORs.
    * keys prepended by m_
    * example -> m_gender:m, m_age:18
    * */
    @Override
    protected String formatUserParams(HashMap<String, Object> params) {
        StringBuilder formattedParams = new StringBuilder();
        for(String key : params.keySet()){
            formattedParams.append(formatKeyValue(key,params.get(key)));
            formattedParams.append(" AND ");//may need to remove the last AND
        }
        return formattedParams.toString();
    }

    @Override
    public String fetchUserParams() {
        return (String) super.fetchUserParams();
    }

    @Override
    protected String formatKey(String key){
        return "m_"+key;
    }
    @Override
    protected String formatValue(Object value){
        return (String) value;
    }

    private String formatKeyValue(String k, Object v){
        return formatKey(k) + ":" + formatValue(v);
    }

}

