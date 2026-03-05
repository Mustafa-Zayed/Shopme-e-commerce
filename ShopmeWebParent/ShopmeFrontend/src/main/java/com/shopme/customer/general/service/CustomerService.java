package com.shopme.customer.general.service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.setting.general.utility.EmailSettingBag;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.customer.country.repository.CountryRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final SettingService settingService;
    private final PasswordEncoder passwordEncoder;
    private final CountryRepository countryRepository;

    public boolean checkEmailUniqueness(String email) {
        return customerRepository.findByEmail(email) == null;
    }

    public boolean checkFullNameUniqueness(String fullName, Integer id) {
        Customer byFullName = customerRepository.findByFullName(fullName);
        if (byFullName == null)
            return true;

        // Check if the email belongs to the edited customer (i.e. edited customer needn't change the full name)
        // If new customer case, returns false as the id param is null
        return byFullName.getId().equals(id);
    }

    public boolean checkPhoneNumberUniqueness(String phoneNumber, Integer id) {
        Customer byPhoneNumber = customerRepository.findByPhoneNumber(phoneNumber);
        if (byPhoneNumber == null)
            return true;

        return byPhoneNumber.getId().equals(id);
    }

    @Transactional
    public void saveCustomer(Customer customer, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        encodePassword(customer);

        // disable customer by default
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(AuthenticationType.DATABASE); // default authentication type

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

    public Customer findByEmail(String email){
        return customerRepository.findByEmail(email);
    }

    public void saveCustomerUponOAuth2Login(String name, String email, String countryCode,
                                            AuthenticationType authenticationType){
        Customer customer = Customer.builder()
                .email(email)
                .authenticationType(authenticationType)
                .enabled(true)
                .createdTime(new Date())
                .password("")
                .addressLine1("")
                .addressLine2("")
                .postalCode("")
                .city("")
                .state("")
                .country(countryRepository.findByCode(countryCode)) // country code is extracted from locale, not accurate, but for now it is the best we can do.
                .build();

        setFirstAndLastName(customer, name);

        if (!checkPhoneNumberUniqueness("", null)) {
            customer.setPhoneNumber("rd" + System.currentTimeMillis()/1000);
        } else {
            customer.setPhoneNumber("");
        }
        customerRepository.save(customer);
    }

    private void setFirstAndLastName(Customer customer, String name) {
        String[] nameParts = name.split(" ");
        if (nameParts.length < 2) {
            StringBuilder firstName = new StringBuilder(name);
            boolean uniqueness = checkFullNameUniqueness(String.valueOf(firstName), null);
            if (!uniqueness) {
                firstName.append(System.currentTimeMillis());
            }
            customer.setFirstName(String.valueOf(firstName));
            customer.setLastName("");
        } else {
            String firstName = nameParts[0];
            StringBuilder lastName = new StringBuilder(name.replaceFirst(firstName + " ", ""));
            boolean uniqueness = checkFullNameUniqueness(firstName + " " + lastName, null);
            if (!uniqueness) {
                lastName.append(System.currentTimeMillis());
            }
            customer.setFirstName(firstName);
            customer.setLastName(String.valueOf(lastName));
        }
    }

    @Transactional
    public void updateCustomerAccountDetails(Customer customer, RedirectAttributes redirectAttributes) {
        Customer customerInDB = customerRepository.findById(customer.getId()).get();
        if (customer.getPassword() == null || customer.getPassword().isEmpty()) // set password = "" to avoid null check
            customer.setPassword(customerInDB.getPassword());
        else
            encodePassword(customer);


        customer.setCreatedTime(customerInDB.getCreatedTime());
        customer.setVerificationCode(customerInDB.getVerificationCode());
        customer.setEnabled(customerInDB.isEnabled());
        customer.setAuthenticationType(customerInDB.getAuthenticationType());
        customer.setResetPasswordToken(customerInDB.getResetPasswordToken());

        String message = "Your Account details have been updated successfully!";
        redirectAttributes.addFlashAttribute("message", message);
        customerRepository.save(customer);
    }

    @Transactional
    public String generateResetPasswordToken(String email) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new CustomerNotFoundException("Couldn't find any customer with the email: " + email);
        }
        String token = UUID.randomUUID().toString(); // RandomString.make(64);
        customer.setResetPasswordToken(token);
        customerRepository.save(customer);
        return token;
    }

    public void sendResetPasswordLink(String email, String resetURL) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBag emailSettingBag = settingService.getEmailSettingsBag();
        JavaMailSender mailSender = EmailConfig.prepareMailSender(emailSettingBag);

        Customer customer = customerRepository.findByEmail(email);
        String fromAddress = emailSettingBag.getFromAddress();
        String senderName = emailSettingBag.getSenderName();
        String subject = "Here's the link to reset your password";
        String content = "<p>Hello, " + customer.getFullName() + "</p>" +
                "<p>You have requested to reset your password.</p>" +
                "<p>Click the link below to reset your password</p>" +
                "<p><a href=\"" + resetURL + "\">Change my password</a></p>" +
                "<br>" +
                "<p>Ignore this email if you do remember your password, " +
                "or you have not made the request.</p>" +
                "<p>Thank you.";

        // Create the email template
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(email);
        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public Customer findByResetPasswordToken(String token) {
        return customerRepository.findByResetPasswordToken(token);
    }

    @Transactional
    public void updateCustomerNewPassword(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customerRepository.updateNewPasswordAndClearResetPasswordToken(customer.getId(), encodedPassword);
    }
}

