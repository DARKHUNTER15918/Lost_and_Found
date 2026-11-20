package com.campus.lostfound.config;

import com.campus.lostfound.entity.*;
import com.campus.lostfound.repository.CategoryRepository;
import com.campus.lostfound.repository.ClaimRequestRepository;
import com.campus.lostfound.repository.ItemReportRepository;
import com.campus.lostfound.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ItemReportRepository itemReportRepository;
    private final ClaimRequestRepository claimRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ItemReportRepository itemReportRepository,
                           ClaimRequestRepository claimRequestRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.itemReportRepository = itemReportRepository;
        this.claimRequestRepository = claimRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedCategories();
        User admin = seedAdminUser();
        User student = seedStudentUser();
        seedSampleReports(student, admin);
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> categories = Arrays.asList(
                new Category("Electronics & Gadgets", "Laptops, chargers, headphones, smartphones, tablets, flash drives", "bi-laptop"),
                new Category("IDs, Cards & Documents", "College ID cards, licenses, ATM cards, certificates, documents", "bi-card-heading"),
                new Category("Books & Stationery", "Textbooks, notebooks, calculators, stationery pouches, geometry boxes", "bi-book"),
                new Category("Keys & Wallets", "Room keys, vehicle keys, wallets, coin purses, cardholders", "bi-wallet2"),
                new Category("Bags & Backpacks", "College bags, laptop backpacks, gym duffels, side bags", "bi-backpack"),
                new Category("Clothing & Wearables", "Jackets, hoodies, caps, watches, jewelry, eyeglasses", "bi-watch"),
                new Category("Sports & Fitness", "Badminton rackets, sports shoes, gym bottles, yoga mats", "bi-bicycle"),
                new Category("Others & Miscellaneous", "Umbrellas, water bottles, lunchboxes, personal accessories", "bi-box-seam")
            );
            categoryRepository.saveAll(categories);
        }
    }

    private User seedAdminUser() {
        return userRepository.findByEmail("admin@campus.edu").orElseGet(() -> {
            User admin = new User();
            admin.setFullName("Campus Security Administrator");
            admin.setEmail("admin@campus.edu");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setStudentId("ADMIN-001");
            admin.setPhone("+1-555-0199");
            admin.setRole(Role.ROLE_ADMIN);
            admin.setEnabled(true);
            return userRepository.save(admin);
        });
    }

    private User seedStudentUser() {
        return userRepository.findByEmail("student@campus.edu").orElseGet(() -> {
            User student = new User();
            student.setFullName("Alex Johnson");
            student.setEmail("student@campus.edu");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setStudentId("CSE-2024-042");
            student.setPhone("+1-555-0142");
            student.setRole(Role.ROLE_STUDENT);
            student.setEnabled(true);
            return userRepository.save(student);
        });
    }

    private void seedSampleReports(User student, User admin) {
        if (itemReportRepository.count() == 0) {
            Category electronics = categoryRepository.findByName("Electronics & Gadgets").orElse(null);
            Category docs = categoryRepository.findByName("IDs, Cards & Documents").orElse(null);
            Category wearables = categoryRepository.findByName("Clothing & Wearables").orElse(null);
            Category books = categoryRepository.findByName("Books & Stationery").orElse(null);

            if (electronics != null && docs != null && wearables != null && books != null) {
                // 1. Lost Item
                ItemReport item1 = new ItemReport();
                item1.setTitle("HP 65W Blue-Pin Laptop Charger");
                item1.setCategory(electronics);
                item1.setDescription("Left on the second floor study table in the Central Library near the reading zone. Has a small yellow tape on the cable wire.");
                item1.setLocation("Central Library, 2nd Floor");
                item1.setItemDate(LocalDate.now().minusDays(2));
                item1.setReportType(ReportType.LOST);
                item1.setStatus(ItemStatus.OPEN);
                item1.setPostedBy(student);
                itemReportRepository.save(item1);

                // 2. Found Item
                ItemReport item2 = new ItemReport();
                item2.setTitle("Silver Analog Wrist Watch (Titan)");
                item2.setCategory(wearables);
                item2.setDescription("Found near the indoor badminton court bench. Metallic strap with minor scratch on the dial clasp. Kept safe at security desk.");
                item2.setLocation("Campus Sports Complex");
                item2.setItemDate(LocalDate.now().minusDays(1));
                item2.setReportType(ReportType.FOUND);
                item2.setStatus(ItemStatus.OPEN);
                item2.setPostedBy(admin);
                itemReportRepository.save(item2);

                // 3. Lost Item
                ItemReport item3 = new ItemReport();
                item3.setTitle("CSE Department Student ID Card - Alex J.");
                item3.setCategory(docs);
                item3.setDescription("Blue lanyard with student ID CSE-2024-042. Slipped out around lunch time near counter 2.");
                item3.setLocation("Campus Cafeteria / Canteen");
                item3.setItemDate(LocalDate.now().minusDays(3));
                item3.setReportType(ReportType.LOST);
                item3.setStatus(ItemStatus.OPEN);
                item3.setPostedBy(student);
                itemReportRepository.save(item3);

                // 4. Found Item
                ItemReport item4 = new ItemReport();
                item4.setTitle("Casio fx-991EX Scientific Calculator");
                item4.setCategory(books);
                item4.setDescription("Found under bench 14 after the afternoon Engineering Mathematics lecture. Black cover with stickers.");
                item4.setLocation("Academic Block B, Room 302");
                item4.setItemDate(LocalDate.now().minusDays(4));
                item4.setReportType(ReportType.FOUND);
                item4.setStatus(ItemStatus.OPEN);
                item4.setPostedBy(student);
                itemReportRepository.save(item4);

                // Seed 1 Sample Claim for demo
                ClaimRequest claim = new ClaimRequest();
                claim.setItemReport(item2);
                claim.setClaimer(student);
                claim.setClaimDescription("This is my Titan chronograph silver watch given by my brother on my birthday.");
                claim.setProofDetails("There is an engraving with initials 'AJ' on the bottom plate and a tiny scuff on the 4 o'clock rim.");
                claim.setContactInfo("alex.johnson@campus.edu or +1-555-0142");
                claim.setStatus(ClaimStatus.PENDING);
                claimRequestRepository.save(claim);
            }
        }
    }
}
