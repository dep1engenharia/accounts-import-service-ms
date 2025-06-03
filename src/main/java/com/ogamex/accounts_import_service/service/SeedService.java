package com.ogamex.accounts_import_service.service;

import com.ogamex.accounts_import_service.entity.User;
import com.ogamex.accounts_import_service.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeedService {

    private final UserRepository userRepo;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    public SeedService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Finds the user by email, then executes two native UPDATE statements:
     *  1) Updates planets for that user
     *  2) Updates users_tech for that user
     *
     * Throws IllegalArgumentException if email is not found.
     */
    @Transactional
    public void seedByEmail(String email) {
        User user = userRepo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        Integer newUserId = user.getId();

        // 2. Update planets for that user_id
        String updatePlanetsSql = ""
                + "UPDATE planets\n"
                + "   SET \n"
                + "     -- Fleets\n"
                + "     light_fighter   = FLOOR(RAND() * (500000 - 500 + 1)) + 500,\n"
                + "     heavy_fighter   = FLOOR(RAND() * (300000 - 300 + 1)) + 300,\n"
                + "     cruiser         = FLOOR(RAND() * (200000 - 200 + 1)) + 200,\n"
                + "     battle_ship     = FLOOR(RAND() * (50000 - 50 + 1)) + 50,\n"
                + "     battlecruiser   = FLOOR(RAND() * (20000 - 20 + 1)) + 20,\n"
                + "     bomber          = FLOOR(RAND() * (10000 - 10 + 1)) + 10,\n"
                + "     destroyer       = FLOOR(RAND() * (5000 - 5 + 1)) + 5,\n"
                + "     deathstar       = FLOOR(RAND() * 100) + 1,\n"
                + "     small_cargo     = FLOOR(RAND() * (10000 - 100 + 1)) + 100,\n"
                + "     large_cargo     = FLOOR(RAND() * (5000 - 50 + 1)) + 50,\n"
                + "     colony_ship     = FLOOR(RAND() * (100 - 1 + 1)) + 1,\n"
                + "     recycler        = FLOOR(RAND() * (1000 - 10 + 1)) + 10,\n"
                + "     espionage_probe = FLOOR(RAND() * (500 - 50 + 1)) + 50,\n"
                + "     solar_satellite = FLOOR(RAND() * (4000 - 10 + 1)) + 10,\n"
                + "\n"
                + "     -- Resources\n"
                + "     metal     = FLOOR(RAND() * (10000000 - 5000000 + 1)) + 5000000,\n"
                + "     crystal   = FLOOR(RAND() * (10000000 - 5000000 + 1)) + 5000000,\n"
                + "     deuterium = FLOOR(RAND() * (10000000 - 5000000 + 1)) + 5000000,\n"
                + "\n"
                + "     -- Defenses\n"
                + "     rocket_launcher          = FLOOR(RAND() * (20000 - 5000 + 1)) + 5000,   \n"
                + "     light_laser              = FLOOR(RAND() * (20000 - 5000 + 1)) + 5000,   \n"
                + "     heavy_laser              = FLOOR(RAND() * (10000 - 2000 + 1)) + 2000,   \n"
                + "     gauss_cannon             = FLOOR(RAND() * (5000 - 1000 + 1)) + 1000,    \n"
                + "     ion_cannon               = FLOOR(RAND() * (2000 - 500 + 1)) + 500,      \n"
                + "     plasma_turret            = FLOOR(RAND() * (800 - 200 + 1)) + 200,       \n"
                + "     small_shield_dome        = FLOOR(RAND() * (500 - 100 + 1)) + 100,       \n"
                + "     large_shield_dome        = FLOOR(RAND() * (200 - 50 + 1)) + 50,         \n"
                + "     anti_ballistic_missile   = FLOOR(RAND() * (1000 - 100 + 1)) + 100,      \n"
                + "     interplanetary_missile   = FLOOR(RAND() * (100 - 10 + 1)) + 10,         \n"
                + "\n"
                + "     -- Facilities (buildings)\n"
                + "     metal_mine                    = FLOOR(RAND() * (20 - 17 + 1)) + 17,   \n"
                + "     metal_mine_percent            = FLOOR(RAND() * 100),                  \n"
                + "     crystal_mine                  = FLOOR(RAND() * (20 - 17 + 1)) + 17,   \n"
                + "     crystal_mine_percent          = FLOOR(RAND() * 100),                  \n"
                + "     deuterium_synthesizer         = FLOOR(RAND() * (20 - 17 + 1)) + 17,   \n"
                + "     deuterium_synthesizer_percent = FLOOR(RAND() * 100),                  \n"
                + "     solar_plant                   = FLOOR(RAND() * (20 - 1 + 1)) + 1,     \n"
                + "     solar_plant_percent           = FLOOR(RAND() * 100),                  \n"
                + "     fusion_plant                  = FLOOR(RAND() * (15 - 1 + 1)) + 1,     \n"
                + "     fusion_plant_percent          = FLOOR(RAND() * 100),                  \n"
                + "     robot_factory                 = FLOOR(RAND() * (10 - 1 + 1)) + 1,     \n"
                + "     nano_factory                  = FLOOR(RAND() * (8 - 1 + 1)) + 1,      \n"
                + "     shipyard                      = FLOOR(RAND() * (5 - 1 + 1)) + 1,      \n"
                + "     research_lab                  = FLOOR(RAND() * (10 - 1 + 1)) + 1,     \n"
                + "     terraformer                   = FLOOR(RAND() * (5 - 1 + 1)) + 1,      \n"
                + "     alliance_depot                = FLOOR(RAND() * (5 - 1 + 1)) + 1,      \n"
                + "     missile_silo                  = FLOOR(RAND() * (3 - 1 + 1)) + 1,      \n"
                + "     space_dock                    = FLOOR(RAND() * (5 - 1 + 1)) + 1,      \n"
                + "     lunar_base                    = FLOOR(RAND() * (3 - 1 + 1)) + 1,      \n"
                + "     sensor_phalanx                = FLOOR(RAND() * (5 - 1 + 1)) + 1,      \n"
                + "     jump_gate                     = FLOOR(RAND() * (2 - 1 + 1)) + 1,      \n"
                + "\n"
                + "     -- Storage (fixed)\n"
                + "     metal_store                   = 20,\n"
                + "     crystal_store                 = 20,\n"
                + "     deuterium_store               = 20\n"
                + "\n"
                + " WHERE user_id = " + newUserId;

        em.createNativeQuery(updatePlanetsSql).executeUpdate();

        // 4. Update users_tech for that same user_id
        String updateTechSql = ""
                + "UPDATE users_tech\n"
                + "   SET\n"
                + "     energy_technology              = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     laser_technology               = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     ion_technology                 = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     hyperspace_technology          = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     plasma_technology              = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     combustion_drive               = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     impulse_drive                  = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     hyperspace_drive               = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     espionage_technology           = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     computer_technology            = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     astrophysics                   = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     intergalactic_research_network = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     graviton_technology            = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     weapon_technology              = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     shielding_technology           = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     armor_technology               = FLOOR(RAND() * (25 - 10 + 1)) + 10,\n"
                + "     updated_at                     = NOW()\n"
                + " WHERE user_id = " + newUserId;

        em.createNativeQuery(updateTechSql).executeUpdate();
    }
}
