package com.flow.FileExtensionBlocking.service;

import com.flow.FileExtensionBlocking.domain.CustomExtension;
import com.flow.FileExtensionBlocking.domain.FixedExtension;
import com.flow.FileExtensionBlocking.repo.CustomExtensionRepo;
import com.flow.FileExtensionBlocking.repo.FixedExtensionRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ExtensionService {
    // 확장자 이름 최대 길이
    private static final int NAME_MAX = 20;
    // 커스텀 확장자 최대 개수
    private static final int CUSTOM_MAX = 200;

    private final FixedExtensionRepo fixedRepo;
    private final CustomExtensionRepo customRepo;

    public ExtensionService(FixedExtensionRepo fixedRepo, CustomExtensionRepo customRepo) {
        this.fixedRepo = fixedRepo;
        this.customRepo = customRepo;
    }

    // 입력받은 문자열을 표준화 (소문자, 앞의 '.' 제거, 허용된 문자만 남김)
    private String norm(String s) {
        String v = (s == null ? "" : s.trim()).toLowerCase();
        if (v.startsWith(".")) v = v.substring(1);
        return v.replaceAll("[^a-z0-9_+-]", "");
    }

    // 확장자 이름 검증 (빈 문자열 또는 최대 길이 초과 시 예외 발생)
    private void validateName(String name) {
        String n = norm(name);
        if (n.isEmpty() || n.length() > NAME_MAX)
            throw new IllegalArgumentException("invalid-length");
    }

    // 고정 확장자 전체 조회
    public List<FixedExtension> listFixed() {
        return fixedRepo.findAll(Sort.by("name"));
    }

    // 고정 확장자 상태(checked) 변경
    public FixedExtension setFixed(String name, boolean checked) {
        String n = norm(name);
        validateName(n);
        FixedExtension fe = fixedRepo.findByName(n).orElseThrow();
        fe.setChecked(checked);
        return fixedRepo.save(fe);
    }

    // 전체 커스텀 확장자 조회 (최근 추가순 정렬)
    public List<CustomExtension> listCustom() {
        return customRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    // 커스텀 확장자 추가
    public String addCustom(String name) {
        String n = norm(name);
        validateName(n);
        if (customRepo.count() >= CUSTOM_MAX) {
            return "200개를 초과하였습니다";
        }
        if (customRepo.existsByName(n) || fixedRepo.existsByName(n)) {
            return "중복된 확장자입니다";
        }
        CustomExtension ce = new CustomExtension();
        ce.setName(n);
        customRepo.save(ce);
        return "success";
    }

    // 커스텀 확장자 삭제
    public void deleteCustom(Long id) {
        customRepo.deleteById(id);
    }

    // 파일 확장자가 차단 대상인지 확인
    public boolean isBlocked(String filename) {
        int pos = filename.lastIndexOf('.');
        String ext = (pos >= 0 && pos < filename.length()-1) ? filename.substring(pos+1) : filename;
        String n = norm(ext);
        if (n.isEmpty()) return false;
        // 고정 확장자가 체크된 경우
        if (fixedRepo.findByName(n).map(FixedExtension::isChecked).orElse(false))
            return true;
        // 커스텀 확장자에 존재하는 경우
        return customRepo.existsByName(n);
    }
}
