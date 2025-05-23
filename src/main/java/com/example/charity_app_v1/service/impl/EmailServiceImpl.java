package com.example.charity_app_v1.service.impl;

import com.example.charity_app_v1.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendOrganizationRegistrationConfirmation(String to, String organizationName, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject("Confirmation d'inscription - " + organizationName);
            mailMessage.setText("Cher administrateur de " + organizationName + ",\n\n" + message + "\n\nCordialement,\nL'équipe Charity App");
            
            emailSender.send(mailMessage);
            logger.info("Email de confirmation envoyé à {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de confirmation", e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de confirmation", e);
        }
    }

    @Override
    public void sendOrganizationApprovalNotification(String to, String organizationName) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject("Votre organisation a été approuvée - " + organizationName);
            mailMessage.setText("Cher administrateur de " + organizationName + ",\n\n" +
                    "Nous sommes heureux de vous informer que votre organisation a été approuvée.\n" +
                    "Vous pouvez maintenant vous connecter à votre tableau de bord et commencer à gérer vos actions caritatives.\n\n" +
                    "Cordialement,\nL'équipe Charity App");
            
            emailSender.send(mailMessage);
            logger.info("Email d'approbation envoyé à {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email d'approbation", e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email d'approbation", e);
        }
    }

    @Override
    public void sendOrganizationRejectionNotification(String to, String organizationName, String reason) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject("Votre organisation n'a pas été approuvée - " + organizationName);
            mailMessage.setText("Cher administrateur de " + organizationName + ",\n\n" +
                    "Nous regrettons de vous informer que votre organisation n'a pas été approuvée.\n" +
                    "Raison : " + reason + "\n\n" +
                    "Vous pouvez modifier vos informations et soumettre à nouveau votre demande.\n\n" +
                    "Cordialement,\nL'équipe Charity App");
            
            emailSender.send(mailMessage);
            logger.info("Email de rejet envoyé à {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de rejet", e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de rejet", e);
        }
    }

    @Override
    public void sendOrganizationSuspensionNotification(String to, String organizationName, String reason) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject("Votre organisation a été suspendue - " + organizationName);
            mailMessage.setText("Cher administrateur de " + organizationName + ",\n\n" +
                    "Nous vous informons que votre organisation a été suspendue.\n" +
                    "Raison : " + reason + "\n\n" +
                    "Vous ne pouvez plus créer de nouvelles actions caritatives pendant la période de suspension.\n" +
                    "Pour plus d'informations, veuillez contacter notre support.\n\n" +
                    "Cordialement,\nL'équipe Charity App");
            
            emailSender.send(mailMessage);
            logger.info("Email de suspension envoyé à {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de suspension", e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de suspension", e);
        }
    }

    @Override
    public void sendOrganizationReactivationNotification(String to, String organizationName) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject("Votre organisation a été réactivée - " + organizationName);
            mailMessage.setText("Cher administrateur de " + organizationName + ",\n\n" +
                    "Nous sommes heureux de vous informer que votre organisation a été réactivée.\n" +
                    "Vous pouvez maintenant reprendre vos activités normalement.\n\n" +
                    "Cordialement,\nL'équipe Charity App");
            
            emailSender.send(mailMessage);
            logger.info("Email de réactivation envoyé à {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de réactivation", e);
            throw new RuntimeException("Erreur lors de l'envoi de l'email de réactivation", e);
        }
    }
} 