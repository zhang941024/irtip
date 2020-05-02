package cn.itrip.common;

import cn.itrip.beans.pojo.ItripUser;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

/**
 * Token验证
 * Created by hanlu on 2017/5/7.
 */
public class ValidationToken {

    private Logger logger = Logger.getLogger(ValidationToken.class);

    private RedisUtil redisAPI;

    public RedisUtil getRedisAPI() {
        return redisAPI;
    }
    public void setRedisAPI(RedisUtil redisAPI) {
        this.redisAPI = redisAPI;
    }
    public ItripUser getCurrentUser(String tokenString){
        //根据token从redis中获取用户信息
			/*
			 test token:
			 key : token:1qaz2wsx
			 value : {"id":"100078","userCode":"myusercode","userPassword":"78ujsdlkfjoiiewe98r3ejrf","userType":"1","flatID":"10008989"}

			*/
        ItripUser itripUser = null;
        if(null == tokenString || "".equals(tokenString)){
            return null;
        }
        try{
            String userInfoJson = redisAPI.getString(tokenString);
            itripUser = JSONObject.parseObject(userInfoJson, ItripUser.class);
        }catch(Exception e){
            itripUser = null;
            logger.error("get userinfo from redis but is error : " + e.getMessage());
        }
        return itripUser;
    }
    public boolean validateToken(String userAgent, String token) throws Exception {
        //判断redis是否存在
        if(!redisAPI.hasKey(token)){
            return false;
        }
        //token后六位和前端传过来的是否一致
        String agentMD5 = token.split("-")[4];
        if(!MD5.getMd5(userAgent,6).equals(agentMD5)){
            return false;
        }
        return true;
    }
}
