package sia.taco_cloud.controller;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import sia.taco_cloud.model.Ingredient;
import sia.taco_cloud.model.Ingredient.Type;
import sia.taco_cloud.repository.IngredientRepository;
import sia.taco_cloud.model.Taco;
import sia.taco_cloud.model.TacoOrder;


@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder")
public class DesignTacoController {
    //контроллер управляет состоянием приложения и предоставляет пользователю интерфейс для создания тако с использованием
// различных ингредиентов
    private final IngredientRepository ingredientRepo;

    @Autowired
    public DesignTacoController(
            IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }


    @ModelAttribute
    public void addIngredientsToModel(Model model) { /* Model – это объект,
        в котором данные пересылаются между контроллером и любым представлением,
        ответственным за преобразование этих данных в разметку HTML*/
        Iterable<Ingredient> ingredients = ingredientRepo.findAll();
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType((List<Ingredient>) ingredients, type));
        }
    }

    @ModelAttribute(name = "tacoOrder")
    public TacoOrder order() {
        return new TacoOrder();
    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    @GetMapping
    public String showDesignForm() {
        return "design";
    }

    private Iterable<Ingredient> filterByType(
            List<Ingredient> ingredients, Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }

    @PostMapping
    public String processTaco(
            @Valid Taco taco, Errors errors,
            @ModelAttribute TacoOrder tacoOrder) {
        if (errors.hasErrors()) {
            return "design";
        }
        tacoOrder.addTaco(taco);
        log.info("Processing taco: {}", taco);
        return "redirect:/orders/current";
    }
}
