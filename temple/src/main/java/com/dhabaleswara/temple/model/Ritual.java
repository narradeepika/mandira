package com.dhabaleswara.temple.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "rituals")
public class Ritual {
    public enum Access { PUBLIC, CLERGY_ONLY, CLOSED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 60) private String timeLabel;
    @NotBlank @Size(max = 120) private String name;
    @Size(max = 255) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Access access = Access.PUBLIC;
    private int sortOrder = 0;
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTimeLabel() { return timeLabel; }
    public void setTimeLabel(String timeLabel) { this.timeLabel = timeLabel; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Access getAccess() { return access; }
    public void setAccess(Access access) { this.access = access; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
