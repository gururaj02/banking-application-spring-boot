package com.example.banking_application_spring_boot.dto;

//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class AccountDto {
//    private Long id;
//    private String accountHolderName;
//    private double balance;
//}

// Record class
// We can use record class instead of DTO's to transfer the data between client and server
// Record class is immutable
public record AccountDto(Long id, String accountHolderName, double balance) {
}
