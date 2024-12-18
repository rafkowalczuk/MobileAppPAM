package com.example.recipesapp.controller;

import com.example.recipesapp.entity.Unit;
import com.example.recipesapp.exceptions.NotAnAuthorException;
import com.example.recipesapp.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/unit")
public class UnitController {
    private final UnitService unitService;

    @GetMapping("/all")
    public ResponseEntity<List<Unit>> getAllUnits() {
        List<Unit> allUnits = unitService.findAllUnits();
        return new ResponseEntity<>(allUnits, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Unit> addUnit(@RequestBody Unit unit) {
        Unit newUnit = unitService.saveUnit(unit);
        return new ResponseEntity<>(newUnit, HttpStatus.OK);
    }

    @GetMapping(value = "{id}")
    public ResponseEntity<Unit> getUnit(@PathVariable final Integer id) {
        Unit unit = unitService.getUnitWithId(id);
        return new ResponseEntity<>(unit, HttpStatus.OK);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public  ResponseEntity<Void> deleteUnit(@PathVariable final Integer id, @AuthenticationPrincipal UserDetails details) {
        Unit unitToDelete = unitService.getUnitWithId(id);
        if (!details.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
            throw new NotAnAuthorException();
        } else {
            unitService.deleteUnit(unitToDelete);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
