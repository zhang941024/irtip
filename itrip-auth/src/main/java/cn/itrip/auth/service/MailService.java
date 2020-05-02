package cn.itrip.auth.service;

public interface MailService {
    void sendMail(String mailAddr,String code) throws Exception;
}
