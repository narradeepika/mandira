package com.dhabaleswara.temple.config;

import com.dhabaleswara.temple.model.*;
import com.dhabaleswara.temple.model.Ritual.Access;
import com.dhabaleswara.temple.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seed(AdminUserRepository users, RitualRepository rituals, FestivalRepository festivals,
                           PasswordEncoder encoder,
                           @Value("${app.admin.username}") String adminUser,
                           @Value("${app.admin.password}") String adminPass) {
        return args -> {
            if (users.findByUsername(adminUser).isEmpty()) {
                AdminUser u = new AdminUser();
                u.setUsername(adminUser);
                u.setPassword(encoder.encode(adminPass));
                u.setRole("ADMIN");
                users.save(u);
                System.out.println(">>> Default admin created: " + adminUser + " (change the password after first login!)");
            }
            if (rituals.count() == 0) {
                r(rituals, 1, "04:30–05:00 AM", "Mangala Alati", "Early morning worship", Access.PUBLIC);
                r(rituals, 2, "05:00–06:00 AM", "Dwarapuja & Mahasnana", "Opening and sacred bathing rituals", Access.PUBLIC);
                r(rituals, 3, "08:30–09:15 AM", "Balabhoga & Morning Aarti", "Morning offering and worship", Access.PUBLIC);
                r(rituals, 4, "11:30 AM–12:00 PM", "Raja Bhoga", "Midday royal offering", Access.CLERGY_ONLY);
                r(rituals, 5, "12:00–01:00 PM", "Rudra Abhisheka", "Midday worship", Access.PUBLIC);
                r(rituals, 6, "01:00–04:00 PM", "Pahada", "Temple doors closed", Access.CLOSED);
                r(rituals, 7, "06:30–07:30 PM", "Sandhya Aarti", "Evening worship", Access.PUBLIC);
                r(rituals, 8, "09:30–10:00 PM", "Shayan Aarti", "Night worship", Access.CLERGY_ONLY);
            }
            if (festivals.count() == 0) {
                f(festivals, 1, "Maha Shivaratri", "A sacred night dedicated to Lord Shiva and devotional worship.", "lucide:moon-star");
                f(festivals, 2, "Bada Osha", "A traditional celebration associated with special offerings and devotion.", "lucide:flame");
                f(festivals, 3, "Sitalasasti", "A celebration connected with the divine marriage of Shiva and Parvati.", "lucide:heart");
                f(festivals, 4, "Makar Sankranti", "A traditional occasion observed with prayers and customary offerings.", "lucide:sun");
                f(festivals, 5, "Kartika Purnima", "An important cultural and religious observance in Odisha.", "lucide:calendar-days");
                f(festivals, 6, "Dola Purnima", "A traditional festival associated with colourful celebrations and worship.", "lucide:flower-2");
            }
        };
    }

    private void r(RitualRepository repo, int order, String time, String name, String desc, Access a) {
        Ritual x = new Ritual();
        x.setSortOrder(order); x.setTimeLabel(time); x.setName(name); x.setDescription(desc); x.setAccess(a);
        repo.save(x);
    }

    private void f(FestivalRepository repo, int order, String name, String desc, String icon) {
        Festival x = new Festival();
        x.setSortOrder(order); x.setName(name); x.setDescription(desc); x.setIcon(icon);
        repo.save(x);
    }
}
