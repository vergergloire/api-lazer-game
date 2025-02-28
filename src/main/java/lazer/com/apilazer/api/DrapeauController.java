package lazer.com.apilazer.api;

import lazer.com.apilazer.model.Capture;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/drapeau")
@CrossOrigin(origins = "*")
public class DrapeauController {

    private final AtomicLong teamRedTime = new AtomicLong(0);
    private final AtomicLong teamBlueTime = new AtomicLong(0);
    private final AtomicLong timeRemaining = new AtomicLong(120000); // 2 minutes initiales
    private boolean gameStarted = false;
    private long gameStartTime = 0;

    /**
     * 📌 Capture du drapeau par une équipe
     */
    @PostMapping("/capture")
    public ResponseEntity<String> captureDrapeau(@RequestBody Capture capture) {
        if (!gameStarted) {
            gameStarted = true;
            gameStartTime = System.currentTimeMillis();
        }

        long now = System.currentTimeMillis();
        long timeHeld = capture.getTimeHeld();

        System.out.println("🛰️ Capture reçue : Équipe " + capture.getTeam() + " - Temps tenu : " + timeHeld + " ms");

        if (capture.getTeam() == 1) {
            teamRedTime.addAndGet(timeHeld);
        } else if (capture.getTeam() == 2) {
            teamBlueTime.addAndGet(timeHeld);
        } else {
            return ResponseEntity.badRequest().body("❌ Équipe invalide !");
        }

        System.out.println("📊 Score mis à jour : Rouge = " + teamRedTime.get() + " ms, Bleue = " + teamBlueTime.get() + " ms");
        return ResponseEntity.ok("✅ Capture enregistrée !");
    }

    /**
     * 📌 Mise à jour du score et du temps restant
     */
    @PostMapping("/status")
    public ResponseEntity<String> updateGameStatus(@RequestBody Map<String, Long> status) {
        teamRedTime.set(status.get("redTime"));
        teamBlueTime.set(status.get("blueTime"));
        timeRemaining.set(status.get("timeRemaining"));

        System.out.println("🔄 Score mis à jour : Rouge = " + teamRedTime.get() + " ms, Bleue = " + teamBlueTime.get() + " ms, Temps restant = " + timeRemaining.get() + " ms");
        return ResponseEntity.ok("✅ Score mis à jour !");
    }

    /**
     * 📌 Récupération du score en temps réel et du vainqueur
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getGameStatus() {
        long remainingTime = Math.max(0, 120000 - (System.currentTimeMillis() - gameStartTime));

        String winner;
        long redTime = teamRedTime.get();
        long blueTime = teamBlueTime.get();

        if (redTime > blueTime) {
            winner = "rouge";
        } else if (blueTime > redTime) {
            winner = "bleue";
        } else {
            winner = null; // Égalité
        }

        Map<String, Object> status = new HashMap<>();
        status.put("rouge", redTime);
        status.put("bleue", blueTime);
        status.put("tempsRestant", remainingTime);
        status.put("vainqueur", winner);

        return ResponseEntity.ok(status);
    }

    /**
     * 📌 Fin de la partie et enregistrement du vainqueur
     */
    @PostMapping("/winner")
    public ResponseEntity<String> declareWinner(@RequestBody Map<String, String> body) {
        System.out.println("🏆 Partie terminée ! Résultat final : " + body.get("message"));
        return ResponseEntity.ok("✅ Résultat reçu !");
    }

    /**
     * 📌 Réinitialisation du jeu
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetGame() {
        teamRedTime.set(0);
        teamBlueTime.set(0);
        timeRemaining.set(120000);
        gameStarted = false;
        gameStartTime = System.currentTimeMillis();

        System.out.println("🔄 Partie réinitialisée !");
        return ResponseEntity.ok("🔄 Partie réinitialisée !");
    }
}

