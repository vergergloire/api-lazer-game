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

    @PostMapping("/capture")
    public ResponseEntity<String> captureDrapeau(@RequestBody Capture capture) {
        System.out.println("🛰️ Capture reçue : Équipe " + capture.getTeam() + " - Temps tenu : " + capture.getTimeHeld() + " ms");

        if (capture.getTeam() == 1) {
            teamRedTime.addAndGet(capture.getTimeHeld());
        } else if (capture.getTeam() == 2) {
            teamBlueTime.addAndGet(capture.getTimeHeld());
        }

        System.out.println("📊 Score mis à jour : Rouge = " + teamRedTime.get() + " ms, Bleue = " + teamBlueTime.get() + " ms");

        return ResponseEntity.ok("✅ Capture enregistrée !");
    }


    @PostMapping("/status")
    public ResponseEntity<String> updateGameStatus(@RequestBody Map<String, Long> status) {
        teamRedTime.set(status.get("redTime"));
        teamBlueTime.set(status.get("blueTime"));
        timeRemaining.set(status.get("timeRemaining"));

        return ResponseEntity.ok("🔄 Score mis à jour !");
    }

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
            winner = null;
        }

        Map<String, Object> status = new HashMap<>();
        status.put("rouge", redTime);
        status.put("bleue", blueTime);
        status.put("tempsRestant", remainingTime);
        status.put("vainqueur", winner);

        return ResponseEntity.ok(status);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetGame() {
        teamRedTime.set(0);
        teamBlueTime.set(0);
        timeRemaining.set(120000);
        gameStarted = false;
        gameStartTime = System.currentTimeMillis();

        return ResponseEntity.ok("🔄 Partie réinitialisée !");
    }
}

