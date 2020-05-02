package cn.itrip.auth.service;

import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.common.MD5;
import cn.itrip.common.RedisUtil;
import cn.itrip.dao.user.ItripUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Resource
    private ItripUserMapper userMapper;
    @Resource
    private SmsService smsService;
    @Resource
    private RedisUtil redisApi;
    @Resource
    private MailService mailService;
    @Override
    public ItripUser findByUserCode(String userCode) {
        HashMap map = new HashMap();
        map.put("userCode",userCode);
        try {
            List<ItripUser> list = userMapper.getItripUserListByMap(map);
            if(list.size() >0){
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ItripUser login(String name, String password) throws Exception {
        ItripUser user = findByUserCode(name);
        if(user != null && user.getUserPassword().equals(password)){
            if(user.getActivated() == 0){
                throw new Exception("用户未激活");
            }
            return user;
        }
        return null;
    }

    @Override
    public void createUserByPhone(ItripUser user) throws Exception {
        //生成一个用户
        userMapper.insertItripUser(user);
        //发短信
        int code = MD5.getRandomCode();//4位的随机数
        smsService.sendSms(user.getUserCode(),"1",new String[]{code+"","1"});
        //保存到redis
        redisApi.setString("activation:"+user.getUserCode(),code+"",10*60);

    }

    @Override
    public boolean validatePhone(String phoneNum, String code) throws Exception {
        String value = redisApi.getString("activation:" + phoneNum);
        //redis的数据和输入的验证码是否一致
        if(value != null && value.equals(code)){
            //激活  更新数据库的数据
            ItripUser user = findByUserCode(phoneNum);
            if(user != null){
                user.setActivated(1);
                user.setFlatID(user.getId());
                user.setUserType(0);
                userMapper.updateItripUser(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public void createUserByMail(ItripUser user) throws Exception {
        //生成激活码
        String code = MD5.getMd5(new Date().toString(),32);
        //发邮件
        mailService.sendMail(user.getUserCode(),code);
        //保存到数据库
        userMapper.insertItripUser(user);
        //激活码保存到redis
        redisApi.setString("activation:"+user.getUserCode(),code,10*60);
    }

    @Override
    public boolean validateMail(String mail, String code) throws Exception {
        String value = redisApi.getString("activation:" + mail);
        if(value != null && value.equals(code)){
            ItripUser user = findByUserCode(mail);
            user.setActivated(1);
            user.setFlatID(user.getId());
            user.setUserType(0);
            userMapper.updateItripUser(user);
            return true;
        }
        return false;
    }
}

