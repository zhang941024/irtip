package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisUtil;
import cn.itrip.common.UserAgentUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Service
public class TokenServiceImpl implements TokenService {
    //token:PC-usercode(md5)-userid-creationdate-random(6)
    @Override
    public String generateToken(String userAgent, ItripUser user) throws Exception {
        StringBuilder str = new StringBuilder("token:");
        //判读哪种客户端
        if(!UserAgentUtil.CheckAgent(userAgent)){
            str.append("PC-");
        }else{
            str.append("MOBILE-");
        }
        //usercode的加密
        str.append(MD5.getMd5(user.getUserCode(),32)+"-");
        //userid
        str.append(user.getId()+"-");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //当前的时间
        str.append(sdf.format(new Date())+"-");
        //6位的随机数
        str.append(MD5.getMd5(userAgent,6));
        return str.toString();
    }
    @Resource
    private RedisUtil redisApi;
    @Override
    public void saveToken(String token, ItripUser user) throws Exception {
        String json = JSONObject.toJSONString(user);
        if(token.startsWith("token:PC-")){//pc端有过期时间（2小时）
            redisApi.setString(token,json,2*60*60);
        }else{//移动端 不过期
            redisApi.setString(token,json);
        }

    }

    @Override
    public boolean validateToken(String userAgent, String token) throws Exception {
        //判断redis是否存在
        if(!redisApi.hasKey(token)){
            return false;
        }
        //token后六位和前端传过来的是否一致
        String agentMD5 = token.split("-")[4];
        if(!MD5.getMd5(userAgent,6).equals(agentMD5)){
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteToken(String token) throws Exception {
        if(redisApi.del(token)){
            return true;
        }
        return false;
    }

    @Override
    public String reloadToken(String userAgent, String token) throws Exception {
        //验证token
        if(!redisApi.hasKey(token)){
            throw new Exception("token无效");
        }
        //计算保护期 token中的第4部分是生成时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long genTime = sdf.parse(token.split("-")[3]).getTime();
        //当前时间 - 生成时间
        long passTime = Calendar.getInstance().getTimeInMillis() - genTime;
        //判断是否在保护期内
        if(passTime < 30*60*1000){
            throw new Exception("token处于保护期，不允许置换，还剩"+(30*60*1000-passTime)/1000+"秒");
        }
        //生成新的
        String newToken = "";
        //获取redis中的value
        ItripUser user = JSONObject.parseObject(redisApi.getString(token),ItripUser.class);
        //redis的有效期
        long ttl = redisApi.getExpire(token);
        if(ttl > 0 || ttl == -1){
            //生成
            newToken = generateToken(userAgent,user);
            //保存
            saveToken(newToken,user);
            //旧的token重新设置有效期
            redisApi.setString(token,JSONObject.toJSONString(user),5*60);
        }else{
            throw new Exception("时间异常，不能置换");
        }
        return newToken;
    }
}
