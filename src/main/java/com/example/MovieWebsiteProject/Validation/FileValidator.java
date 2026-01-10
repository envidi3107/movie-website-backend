package com.example.MovieWebsiteProject.Validation;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

  private long maxSize;
  private String[] allowedTypes;

  @Override
  public void initialize(ValidFile constraintAnnotation) {
    this.maxSize = constraintAnnotation.maxSize();
    this.allowedTypes = constraintAnnotation.allowedTypes();
  }

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    if (file == null || file.isEmpty()) {
      return true;
    }

    if (file.getSize() > maxSize) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("File's size exceeds the allowed limit!")
          .addConstraintViolation();
      return false;
    }

    if (allowedTypes.length > 0
        && Arrays.stream(allowedTypes).noneMatch(type -> type.equals(file.getContentType()))) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate("File's type is invalid!")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
