package com.flow.FileExtensionBlocking.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "fixed_extension")
public class FixedExtension {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=32)
    private String name;

    @Column(nullable=false)
    private boolean checked;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}
