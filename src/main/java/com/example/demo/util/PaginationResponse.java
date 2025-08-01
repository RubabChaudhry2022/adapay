package com.example.demo.util;

import java.util.List;
import org.springframework.data.domain.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {
	private List<T> content;
	private int page;
	private int size;
	private long totalElements;
	private int totalPages;
	private boolean last;

	public static <T> PaginationResponse<T> fromPage(Page<T> pageData) {
		return new PaginationResponse<>(pageData.getContent(), pageData.getNumber(), pageData.getSize(),
				pageData.getTotalElements(), pageData.getTotalPages(), pageData.isLast());
	}
}
