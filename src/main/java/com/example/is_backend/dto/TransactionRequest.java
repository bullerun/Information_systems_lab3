package com.example.is_backend.dto;

import com.example.is_backend.entity.TransactionDesicion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {
    private TransactionDesicion decision;
}
