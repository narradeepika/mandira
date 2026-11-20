package com.dhabaleswara.temple.controller;

import com.dhabaleswara.temple.model.*;
import com.dhabaleswara.temple.repository.*;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final EnquiryRepository enquiries;
    private final RitualRepository rituals;
    private final FestivalRepository festivals;
    private final AdminUserRepository users;
    private final PasswordEncoder encoder;

    public AdminController(EnquiryRepository e, RitualRepository r, FestivalRepository f,
                           AdminUserRepository u, PasswordEncoder p) {
        this.enquiries = e; this.rituals = r; this.festivals = f; this.users = u; this.encoder = p;
    }

    /* ---------- Dashboard ---------- */
    @GetMapping({"", "/"})
    public String dashboard(Model m) {
        m.addAttribute("total", enquiries.count());
        m.addAttribute("open", enquiries.countByResolved(false));
        m.addAttribute("resolved", enquiries.countByResolved(true));
        m.addAttribute("rituals", rituals.count());
        m.addAttribute("festivals", festivals.count());
        m.addAttribute("latest", enquiries.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent());
        m.addAttribute("active", "dashboard");
        return "admin/dashboard";
    }

    /* ---------- Enquiries ---------- */
    @GetMapping("/enquiries")
    public String enquiries(@RequestParam(required = false) String q,
                            @RequestParam(defaultValue = "0") int page, Model m) {
        String term = q == null ? "" : q.trim();
        Page<Enquiry> p = enquiries.search(term, PageRequest.of(Math.max(page, 0), 15, Sort.by(Sort.Direction.DESC, "createdAt")));
        m.addAttribute("page", p);
        m.addAttribute("q", term);
        m.addAttribute("active", "enquiries");
        return "admin/enquiries";
    }

    @GetMapping("/enquiries/{id}")
    public String enquiry(@PathVariable Long id, Model m) {
        m.addAttribute("e", enquiries.findById(id).orElseThrow());
        m.addAttribute("active", "enquiries");
        return "admin/enquiry";
    }

    @PostMapping("/enquiries/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        Enquiry e = enquiries.findById(id).orElseThrow();
        e.setResolved(!e.isResolved());
        enquiries.save(e);
        ra.addFlashAttribute("msg", "Enquiry marked as " + (e.isResolved() ? "resolved." : "open."));
        return "redirect:/admin/enquiries/" + id;
    }

    @PostMapping("/enquiries/{id}/delete")
    public String deleteEnquiry(@PathVariable Long id, RedirectAttributes ra) {
        enquiries.deleteById(id);
        ra.addFlashAttribute("msg", "Enquiry deleted.");
        return "redirect:/admin/enquiries";
    }

    /* ---------- Rituals ---------- */
    @GetMapping("/rituals")
    public String rituals(Model m) {
        m.addAttribute("items", rituals.findAllByOrderBySortOrderAsc());
        m.addAttribute("active", "rituals");
        return "admin/rituals";
    }

    @GetMapping("/rituals/new")
    public String newRitual(Model m) {
        m.addAttribute("ritual", new Ritual());
        m.addAttribute("accesses", Ritual.Access.values());
        m.addAttribute("active", "rituals");
        return "admin/ritual-form";
    }

    @GetMapping("/rituals/{id}/edit")
    public String editRitual(@PathVariable Long id, Model m) {
        m.addAttribute("ritual", rituals.findById(id).orElseThrow());
        m.addAttribute("accesses", Ritual.Access.values());
        m.addAttribute("active", "rituals");
        return "admin/ritual-form";
    }

    @PostMapping("/rituals/save")
    public String saveRitual(@Valid @ModelAttribute("ritual") Ritual ritual, BindingResult br,
                             Model m, RedirectAttributes ra) {
        if (br.hasErrors()) {
            m.addAttribute("accesses", Ritual.Access.values());
            m.addAttribute("active", "rituals");
            return "admin/ritual-form";
        }
        rituals.save(ritual);
        ra.addFlashAttribute("msg", "Ritual saved.");
        return "redirect:/admin/rituals";
    }

    @PostMapping("/rituals/{id}/delete")
    public String deleteRitual(@PathVariable Long id, RedirectAttributes ra) {
        rituals.deleteById(id);
        ra.addFlashAttribute("msg", "Ritual deleted.");
        return "redirect:/admin/rituals";
    }

    /* ---------- Festivals ---------- */
    @GetMapping("/festivals")
    public String festivals(Model m) {
        m.addAttribute("items", festivals.findAllByOrderBySortOrderAsc());
        m.addAttribute("active", "festivals");
        return "admin/festivals";
    }

    @GetMapping("/festivals/new")
    public String newFestival(Model m) {
        m.addAttribute("festival", new Festival());
        m.addAttribute("active", "festivals");
        return "admin/festival-form";
    }

    @GetMapping("/festivals/{id}/edit")
    public String editFestival(@PathVariable Long id, Model m) {
        m.addAttribute("festival", festivals.findById(id).orElseThrow());
        m.addAttribute("active", "festivals");
        return "admin/festival-form";
    }

    @PostMapping("/festivals/save")
    public String saveFestival(@Valid @ModelAttribute("festival") Festival festival, BindingResult br,
                               Model m, RedirectAttributes ra) {
        if (br.hasErrors()) {
            m.addAttribute("active", "festivals");
            return "admin/festival-form";
        }
        festivals.save(festival);
        ra.addFlashAttribute("msg", "Festival saved.");
        return "redirect:/admin/festivals";
    }

    @PostMapping("/festivals/{id}/delete")
    public String deleteFestival(@PathVariable Long id, RedirectAttributes ra) {
        festivals.deleteById(id);
        ra.addFlashAttribute("msg", "Festival deleted.");
        return "redirect:/admin/festivals";
    }

    /* ---------- Change password ---------- */
    @GetMapping("/password")
    public String passwordForm(Model m) {
        m.addAttribute("active", "password");
        return "admin/password";
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam String current, @RequestParam String newPassword,
                                 @RequestParam String confirm, Principal principal,
                                 Model m, RedirectAttributes ra) {
        AdminUser u = users.findByUsername(principal.getName()).orElseThrow();
        String error = null;
        if (!encoder.matches(current, u.getPassword())) error = "Current password is incorrect.";
        else if (newPassword.length() < 10) error = "New password must be at least 10 characters.";
        else if (!newPassword.equals(confirm)) error = "New passwords do not match.";
        if (error != null) {
            m.addAttribute("error", error);
            m.addAttribute("active", "password");
            return "admin/password";
        }
        u.setPassword(encoder.encode(newPassword));
        users.save(u);
        ra.addFlashAttribute("msg", "Password updated.");
        return "redirect:/admin";
    }
}
