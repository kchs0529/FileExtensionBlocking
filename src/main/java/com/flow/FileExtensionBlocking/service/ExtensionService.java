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
    private static final int NAME_MAX = 20;
    private static final int CUSTOM_MAX = 200;

    private final FixedExtensionRepo fixedRepo;
    private final CustomExtensionRepo customRepo;

    public ExtensionService(FixedExtensionRepo fixedRepo, CustomExtensionRepo customRepo) {
        this.fixedRepo = fixedRepo;
        this.customRepo = customRepo;
    }

    private String norm(String s) {
        String v = (s == null ? "" : s.trim()).toLowerCase();
        if (v.startsWith(".")) v = v.substring(1);
        return v.replaceAll("[^a-z0-9_+-]", "");
    }

    private void validateName(String name) {
        String n = norm(name);
        if (n.isEmpty() || n.length() > NAME_MAX)
            throw new IllegalArgumentException("invalid-length");
    }

    public List<FixedExtension> listFixed() {
        return fixedRepo.findAll(Sort.by("name"));
    }

    public FixedExtension setFixed(String name, boolean checked) {
        String n = norm(name); validateName(n);
        FixedExtension fe = fixedRepo.findByName(n).orElseThrow();
        fe.setChecked(checked);
        return fixedRepo.save(fe);
    }

    //전체 커스텀 리스트 가져오기
    public List<CustomExtension> listCustom() {
        return customRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public CustomExtension addCustom(String name) {
        String n = norm(name); validateName(n);
        if (customRepo.count() >= CUSTOM_MAX) throw new IllegalStateException("limit-200");
        if (customRepo.existsByName(n) || fixedRepo.existsByName(n)) throw new IllegalStateException("duplicate");
        CustomExtension ce = new CustomExtension(); ce.setName(n);
        return customRepo.save(ce);
    }

    //확장자 custom 삭제하기
    public void deleteCustom(Long id) {
        customRepo.deleteById(id);
    }


    public boolean isBlocked(String filename) {
        int pos = filename.lastIndexOf('.');
        String ext = (pos >= 0 && pos < filename.length()-1) ? filename.substring(pos+1) : filename;
        String n = norm(ext);
        if (n.isEmpty()) return false;
        if (fixedRepo.findByName(n).map(FixedExtension::isChecked).orElse(false)) return true;
        return customRepo.existsByName(n);
    }
}
