package com.unicam.bandb.prenotazione;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "api/v1/prenotazione")
public class PrenotazioneController {


    private final PrenotazioneService prenotazioneService;

    public PrenotazioneController(PrenotazioneService prenotazioneService) {
        this.prenotazioneService = prenotazioneService;
    }

    @GetMapping("/getDate")
    public List<Prenotazione> getDate() throws Exception { return prenotazioneService.getDate(); }


    @GetMapping("/getData")
    public List<Prenotazione> getPrenotazioneByEmail (@RequestParam String email) throws Exception {
        return prenotazioneService.getPrenotazioneByEmail(email);
    }

    @DeleteMapping("/delete")
    public void deleteUserByEmail (@RequestParam String email) throws Exception {
        prenotazioneService.deleteUserByEmail(email);
    }

    @GetMapping("/getAdmin")
    public Boolean getAdminByEmail (@RequestParam String email) {
        return prenotazioneService.getAdminByEmail(email);
    }

    @PutMapping("/admin")
    public void AdminUserByEmail (@RequestParam String email) throws Exception {
        prenotazioneService.adminUserByEmail(email);
    }

    @PostMapping("/add")
    public ResponseEntity<Prenotazione> registerNewPrenotazione(@RequestBody Prenotazione prenotazione){
        Prenotazione newPrenotazione = prenotazioneService.addNewPrenotazione(prenotazione);
        return new ResponseEntity<>(newPrenotazione, HttpStatus.CREATED);
    }

    @GetMapping(value = "/healthcheck")
    @ResponseStatus(HttpStatus.OK)
    public void healthcheck(){
    }

}
