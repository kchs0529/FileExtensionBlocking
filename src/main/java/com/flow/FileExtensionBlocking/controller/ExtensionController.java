package com.flow.FileExtensionBlocking.controller;

import com.flow.FileExtensionBlocking.domain.CustomExtension;
import com.flow.FileExtensionBlocking.domain.FixedExtension;
import com.flow.FileExtensionBlocking.service.ExtensionService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ExtensionController {
    private final ExtensionService svc;
    public ExtensionController(ExtensionService svc) { this.svc = svc; }

    @GetMapping("/fixed")
    public List<FixedExtension> fixed() { return svc.listFixed(); }

    @PatchMapping("/fixed")
    public FixedExtension patchFixed(@RequestBody Map<String,Object> body) {
        String name = (String) body.get("name");
        Boolean checked = (Boolean) body.get("checked");
        return svc.setFixed(name, Boolean.TRUE.equals(checked));
    }

    @GetMapping("/custom")
    public List<CustomExtension> custom() { return svc.listCustom(); }

    @PostMapping("/custom")
    public CustomExtension add(@RequestBody Map<String,String> body) {
        return svc.addCustom(body.get("name"));
    }

    @DeleteMapping("/custom/{id}")
    public void del(@PathVariable Long id) { svc.deleteCustom(id); }

    @GetMapping("/validate")
    public Map<String,Object> validate(@RequestParam String filename) {
        boolean blocked = svc.isBlocked(filename);
        return Map.of("filename", filename, "blocked", blocked);
    }
}
