package com.shopme.setting.filter;

import com.shopme.common.entity.setting.Setting;
import com.shopme.setting.service.SettingService;
import jakarta.servlet.*;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SettingFilter implements Filter {

    private final SettingService settingService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String requestURL = servletRequest.getRequestURL().toString();

        if (requestURL.contains(".css") || requestURL.contains(".js") ||
                requestURL.contains(".png") || requestURL.contains(".jpg")) {
            chain.doFilter(request, response);
            return;
        }

        List<Setting> settings = settingService.findGeneralAndCurrencySettings();
        settings.forEach(setting -> {
            // System.out.println(setting);
            request.setAttribute(setting.getKey(), setting.getValue());
        });

        chain.doFilter(request, response);
    }
}
