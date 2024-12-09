package com.work_service.work.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedBookResponse {
    private Long bookId;
    private String title;
    private Integer purchaseCount;
}
