package com.upmusic.web.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailServiceImpl implements EmailService {
  
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	public TemplateEngine templateEngine;
	
    @Autowired
    public JavaMailSender emailSender;
 

	@Override
	public void sendEmail(String to, String subject, String code) {
		logger.debug("sendEmail called : to={}, subject={}, text={}", to, subject, code);
		try {
			char[] chars = code.toCharArray();
			MimeMessagePreparator messagePreparator = mimeMessage -> {
		        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
		        messageHelper.setFrom("upmusic.glosfer@gmail.com");
		        messageHelper.setTo(to);
		        messageHelper.setSubject(subject);
		        String content = build(chars);
		        messageHelper.setText(content, true);
		    };
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(to);
//            message.setSubject(subject);
//            message.setText(text);
            emailSender.send(messagePreparator);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
	}

	@Override
	public void sendEmailUsingTemplate(String to, String subject, SimpleMailMessage template, String ...templateArgs) {
		String code = String.format(template.getText(), (Object) templateArgs);  
		sendEmail(to, subject, code);
	}
	
	private String build(char[] chars) {
        Context context = new Context();
        context.setVariable("message", chars);
        return templateEngine.process("mail_template", context);
    }
	
}
