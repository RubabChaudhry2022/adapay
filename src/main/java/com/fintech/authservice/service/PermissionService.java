package com.fintech.authservice.service;

import com.fintech.authservice.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

	private final PermissionRepository permissionRepository;

	public boolean isAllowed(String method, String path, String role) {
		String normalizedPath = normalizePath(path);

		return permissionRepository.findByMethod(method).stream().anyMatch(
				p -> doesPathMatch(normalizedPath, p.getPath()) && p.getRoles().replaceAll("\\s", "").contains(role));
	}

	private String normalizePath(String path) {
		return path.replaceAll("/\\d+", "/{id}");
	}

	private boolean doesPathMatch(String requestPath, String permissionPath) {
		return requestPath.equalsIgnoreCase(permissionPath);
	}
}
