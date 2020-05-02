package cn.itrip.auth.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MailServiceImpl implements MailService {
    @Resource
    private SimpleMailMessage message;
    @Resource
    private MailSender mailSender;
    @Override
    public void sendMail(String mailAddr, String code) throws Exception {
        message.setTo(mailAddr);
        message.setText(code);
        mailSender.send(message);

    }
}
