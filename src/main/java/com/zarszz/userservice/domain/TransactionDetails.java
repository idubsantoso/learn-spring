package com.zarszz.userservice.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class TransactionDetails {
	private String code;
	private BigDecimal grossAmount;
}
