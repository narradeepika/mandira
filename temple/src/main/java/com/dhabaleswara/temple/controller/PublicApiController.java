package com.dhabaleswara.temple.controller;

import com.dhabaleswara.temple.dto.EnquiryRequest;
import com.dhabaleswara.temple.model.*;
import com.dhabaleswara.temple.repository.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PublicApiController {
    private final EnquiryRepository enquiries;
    private final RitualRepository rituals;
    private final FestivalRepository festivals;

    public PublicApiController(EnquiryRepository e, RitualRepository r, FestivalRepository f) {
        this.enquiries = e; this.rituals = r; this.festivals = f;
    }

    @PostMapping("/enquiries")
    public ResponseEntity<Map<String, String>> submit(@Valid @RequestBody EnquiryRequest req) {
        Enquiry e = new Enquiry();
        e.setName(req.getName().trim());
        e.setEmail(req.getEmail().trim());
        e.setPhone(req.getPhone() == null ? null : req.getPhone().trim());
        e.setSubject(req.getSubject().trim());
        e.setMessage(req.getMessage().trim());
        enquiries.save(e);
        return ResponseEntity.ok(Map.of("message", "Thank you! Your enquiry has been received."));
    }

    @GetMapping("/rituals")
    public List<Ritual> rituals() { return rituals.findByActiveTrueOrderBySortOrderAsc(); }

    @GetMapping("/festivals")
    public List<Festival> festivals() { return festivals.findByActiveTrueOrderBySortOrderAsc(); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> invalid(MethodArgumentNotValidException ex) {
        FieldError fe = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String msg = fe == null ? "Invalid request." : fe.getField() + ": " + fe.getDefaultMessage();
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }
}
