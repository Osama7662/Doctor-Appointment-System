package com.appointment_service.Dto;
import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponseDto<T> {

    private List<T> content;

    private int pageNo;
    private int pageSize;

    private long totalElements;
    private int totalPages;

    private boolean firstPage;
    private boolean lastPage;
}
