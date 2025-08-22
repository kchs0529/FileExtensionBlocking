package com.flow.FileExtensionBlocking.controller;

import com.flow.FileExtensionBlocking.domain.CustomExtension;
import com.flow.FileExtensionBlocking.domain.FixedExtension;
import com.flow.FileExtensionBlocking.service.ExtensionService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ExtensionController {
    private final ExtensionService service;
    public ExtensionController(ExtensionService service) { this.service = service; }

    @GetMapping("/fixed")
    public List<FixedExtension> fixed() { return service.listFixed(); }

    @PatchMapping("/fixed")
    public FixedExtension patchFixed(@RequestBody Map<String,Object> body) {
        String name = (String) body.get("name");
        Boolean checked = (Boolean) body.get("checked");
        return service.setFixed(name, Boolean.TRUE.equals(checked));
    }

    @GetMapping("/custom")
    public List<CustomExtension> custom() { return service.listCustom(); }

    @PostMapping("/custom")
    public String add(@RequestBody Map<String,String> body) {
        return service.addCustom(body.get("name"));
    }

    @DeleteMapping("/custom/{id}")
    public void del(@PathVariable Long id) { service.deleteCustom(id); }

    @GetMapping("/validate")
    public Map<String,Object> validate(@RequestParam String filename) {
        boolean blocked = service.isBlocked(filename);
        return Map.of("filename", filename, "blocked", blocked);
    }
}
