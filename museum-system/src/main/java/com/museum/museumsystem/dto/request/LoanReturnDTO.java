package com.museum.museumsystem.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanReturnDTO {
    private Long loanId;
    private LocalDate actualReturnDate;
}