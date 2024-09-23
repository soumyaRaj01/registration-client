package io.mosip.registration.util.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 kar Nin validator for validating with new regex pattern of nin 
 
 */
@Component
public class NinValidator {
	@Value("${mosip.registration.util.common.nin.regex:^[a-zA-Z0-9]{14,14}}")
	private String regexPattern;
	public boolean validate(String input) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);
        if(matcher.matches()) {
        	return true;
        }
        else {
        	throw new InvalidIDException("NIN 1","Enter a Valid NIN");
        }
    }
}
