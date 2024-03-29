package com.gestion.controller;

import com.gestion.model.Reparto;
import com.gestion.repository.RepartoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/reparto")
public class RepartoController {

    private final RepartoRepository repository;

    @Autowired
    public RepartoController(RepartoRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/{id}")
    public ResponseEntity<Reparto> getReparto(@PathVariable Long id) {

        Optional<Reparto> optionalReparto = repository.findById(id);
        return new ResponseEntity<>(optionalReparto.get(), HttpStatus.OK);
    }

    @RequestMapping("/numero/{nroReparto}")
    public ResponseEntity<List<Reparto>> getRepartosByNroReparto(@PathVariable int nroReparto) {

        List<Reparto> repartos = repository.findAllByNroRepartoAndActivoTrueOrderByNroReparto(nroReparto);

        if (repartos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(repartos, HttpStatus.OK);
    }

    @RequestMapping("/dia_semana/{diaSemana}")
    public ResponseEntity<List<Reparto>> getRepartosdDiaSemana(@PathVariable String diaSemana) {

        List<Reparto> repartos = repository.findAllByDiaSemanaAndActivoTrue(diaSemana);

        if (repartos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(repartos, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Reparto> crearReparto(@RequestBody Reparto reparto) {
        try {
            Reparto nuevoReparto = repository
                    .save(new Reparto(reparto.getNroReparto(), reparto.getDiaSemana(), reparto.getZonaEntrega()));

            return new ResponseEntity<>(nuevoReparto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/all")
    public ResponseEntity<List<Reparto>> getRepartos(){
        try {
            List<Reparto> repartos = repository.findAllByActivoTrueOrderByNroRepartoAscDiaSemanaAsc();
                    /*repository.findAll(Sort.by(Sort.Direction.ASC,"nroReparto")).stream()
                    .filter(Reparto::isActivo)
                    .collect(Collectors.toList());
                     */
            if (repartos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(repartos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/numeros")
    public ResponseEntity<List<Integer>> getNroRepartos(){
        try {
            List<Reparto> repartos = repository.findAll(Sort.by(Sort.Direction.ASC,"nroReparto")).stream()
                    .filter(Reparto::isActivo)
                    .collect(Collectors.toList());
            List<Integer> nroRepartos = new ArrayList<>();
            for (Reparto reparto : repartos){
                if (!nroRepartos.contains(reparto.getNroReparto())){
                    nroRepartos.add(reparto.getNroReparto());
                }
            }
            if (repartos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(nroRepartos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/diaSemana/{nroReparto}")
    public ResponseEntity<Set<String>> getDiasSemanasDisponibles(@PathVariable int nroReparto){
        try {
            Set<String> diasSemana = new HashSet<>(Arrays.asList("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"));
            List<Reparto> repartos = repository.findAllByNroRepartoAndActivoTrueOrderByNroReparto(nroReparto);

            for (Reparto reparto : repartos){
                diasSemana.remove(reparto.getDiaSemana());
            }
            if (repartos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(diasSemana, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reparto> updateReparto(@RequestBody Reparto newReparto, @PathVariable Long id){

        return repository.findById(id)
                .map(reparto -> {
                    reparto.setNroReparto(newReparto.getNroReparto());
                    reparto.setDiaSemana(newReparto.getDiaSemana());
                    return new ResponseEntity<>(repository.save(reparto), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newReparto), HttpStatus.CREATED));
    }

    @RequestMapping("delete/{id}")
    public ResponseEntity deleteReparto(@PathVariable Long id){

        return repository.findById(id)
                .map(reparto -> {
                    reparto.setActivo(false);
                    repository.save(reparto);
                    return new ResponseEntity(HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/edit")
    public ResponseEntity<Reparto> editReparto(@RequestBody Reparto newReparto) {
        return repository.findById(newReparto.getId())
                .map(reparto -> {
                    reparto.setNroReparto(newReparto.getNroReparto());
                    reparto.setDiaSemana(newReparto.getDiaSemana());
                    reparto.setZonaEntrega(newReparto.getZonaEntrega());
                    return new ResponseEntity<>(repository.save(reparto), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(repository.save(newReparto), HttpStatus.CREATED));
    }
}

