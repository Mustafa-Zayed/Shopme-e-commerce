package com.shopme.customer.general.service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.setting.general.utility.EmailSettingBag;
import com.shopme.customer.general.repository.CustomerRepository;
import com.shopme.setting.service.SettingService;
import com.shopme.utitlity.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final SettingService settingService;
    private final PasswordEncoder passwordEncoder;

    public boolean checkEmailUniqueness(String email) {
        return customerRepository.findByEmail(email) == null;
    }

    public boolean checkFullNameUniqueness(String fullName) {
        return customerRepository.findByFullName(fullName) == null;
    }

    public boolean checkPhoneNumberUniqueness(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber) == null;
    }

    @Transactional
    public void saveCustomer(Customer customer, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        encodePassword(customer);

        // disable customer by default
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());

        // generate random verification code
        String randomCode = UUID.randomUUID().toString(); // RandomString.make(64);
        customer.setVerificationCode(randomCode);

        // send verification email
        sendVerificationEmail(customer, request);

        customerRepository.save(customer);
    }

    private void encodePassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
    }

    private void sendVerificationEmail(Customer customer, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettingBag = settingService.getEmailSettingsBag();
        JavaMailSender mailSender = EmailConfig.prepareMailSender(emailSettingBag);

        String toAddress = customer.getEmail();
        String fromAddress = emailSettingBag.getFromAddress();
        String senderName = emailSettingBag.getSenderName();
        String subject = emailSettingBag.getCustomerVerifySubject();
        String content = emailSettingBag.getCustomerVerifyContent();

        // Create the email template
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        // replace placeholders with actual values
        String siteURL = EmailConfig.getSiteURL(request);
        String verifyURL = siteURL + "/register/verify?code=" + customer.getVerificationCode();

        content = content.replace("[[name]]", customer.getFullName());
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @Transactional // required if we use @Modifying approach
    public boolean verifyCustomer(String verificationCode) {
        Customer customer = customerRepository.findByVerificationCode(verificationCode);
        if (customer == null || customer.isEnabled())
            return false;

        customerRepository.enable(customer.getId());
        return true;
    }

    @Transactional
    public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
        if (customer.getAuthenticationType().equals(authenticationType))
            return;

        customerRepository.updateAuthenticationType(customer.getId(), authenticationType);
    }
}
