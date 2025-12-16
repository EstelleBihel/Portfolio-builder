package alt.portfolio.builder.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import alt.portfolio.builder.services.RubricService;

@Controller
public class RubricController {

    @Autowired
    private RubricService rubricService;

    // Route pour ajouter une rubrique à un profil spécifique
    // On reçoit l'ID du profil dans l'URL et le NOM de la rubrique dans le corps du formulaire
    @PostMapping("/profiles/{profileId}/rubrics/add")
    public String addRubric(@PathVariable UUID profileId, @RequestParam String name) {
        
        // Appel au service
        rubricService.createRubric(profileId, name);
        
        // Redirection vers la page d'édition du profil pour voir la nouvelle rubrique
        //ProfileController => route : /profiles/{id}/edit)
        return "redirect:/profiles/" + profileId + "/edit";
    }
    
    //A faire = ajout méthode pour supprimer une rubrique US014
}