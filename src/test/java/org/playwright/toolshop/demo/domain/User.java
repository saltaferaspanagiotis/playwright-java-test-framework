package org.playwright.toolshop.demo.domain;

import com.github.javafaker.Faker;

import java.text.SimpleDateFormat;

/* Payload
{
  "first_name": "John",
  "last_name": "Doe",
  "address": {
    "street": "Street 1",
    "house_number": "12",
    "city": "City",
    "state": "State",
    "country": "Country",
    "postal_code": "1234AA"
  },
  "phone": "0987654321",
  "dob": "1970-01-01",
  "password": "SuperSecure@123",
  "email": "john@doe.example"
}
 */

public record User(
        String first_name,
        String last_name,
        Address address,
        String phone,
        String dob,
        String password,
        String email
) {

    public record Address(
            String street,
            String house_number,
            String city,
            String state,
            String country,
            String postal_code
    ) {
    }

    public static User randomValidUser() {
        Faker faker = new Faker();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(faker.date().birthday());
        String validPassword = validatePassword(faker.internet().password());
        Address address = new Address(
                faker.address().streetName(),
                faker.address().buildingNumber(),
                faker.address().city(),
                faker.address().state(),
                "Greece",
                faker.address().zipCode()
        );
        String formattedPhoneNumber = faker.phoneNumber().phoneNumber()
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(".", "")
                .replace(" ", "")
                .replace("x","");

        return new User(
                faker.name().firstName(),
                faker.name().lastName(),
                address,
                formattedPhoneNumber,
                formattedDate,
                validPassword,
                faker.internet().emailAddress()
        );
    }

    private static String validatePassword(String pass) {
        boolean hasUpper = pass.matches(".*[A-Z].*");
        boolean hasLower = pass.matches(".*[a-z].*");
        boolean hasSymbol = pass.matches(".*[^a-zA-Z0-9].*");
        boolean hasNumber = pass.matches(".*\\d.*");
        boolean hasMinLength = pass.length() >= 8;

        StringBuilder validPasswordBuilder = new StringBuilder(pass);
        if (!hasUpper) {
            validPasswordBuilder.append('A');
        }
        if (!hasLower) {
            validPasswordBuilder.append('a');
        }
        if (!hasSymbol) {
            validPasswordBuilder.append('@');
        }
        if (!hasNumber) {
            validPasswordBuilder.append('0');
        }
        if (!hasMinLength) {
            validPasswordBuilder.append("aaa");
        }
        return validPasswordBuilder.toString();
    }
}
