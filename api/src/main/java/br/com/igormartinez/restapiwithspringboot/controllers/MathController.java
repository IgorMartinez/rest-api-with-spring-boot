package br.com.igormartinez.restapiwithspringboot.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.igormartinez.restapiwithspringboot.utils.SimpleMath;

@RestController
@RequestMapping("/math")
public class MathController {

    SimpleMath math = new SimpleMath();

    @RequestMapping(value = "/sum/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double sum(@PathVariable(value = "numberOne") Double numberOne,
            @PathVariable(value = "numberTwo") Double numberTwo) {
        return math.sum(numberOne, numberTwo);
    }

    @RequestMapping(value = "/subtraction/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double sub(@PathVariable(value = "numberOne") Double numberOne,
            @PathVariable(value = "numberTwo") Double numberTwo) {
        return math.subtraction(numberOne, numberTwo);
    }

    @RequestMapping(value = "/multiplication/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double mult(@PathVariable(value = "numberOne") Double numberOne,
            @PathVariable(value = "numberTwo") Double numberTwo) {
        return math.multiplication(numberOne, numberTwo);
    }

    @RequestMapping(value = "/division/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double div(@PathVariable(value = "numberOne") Double numberOne,
            @PathVariable(value = "numberTwo") Double numberTwo) {
        return math.division(numberOne, numberTwo);
    }

    @RequestMapping(value = "/average/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double avr(@PathVariable(value = "numberOne") Double numberOne,
            @PathVariable(value = "numberTwo") Double numberTwo) {
        return math.average(numberOne, numberTwo);
    }

    @RequestMapping(value = "/squareroot/{number}", method = RequestMethod.GET)
    public Double sqrt(@PathVariable(value = "number") Double number) {
        return math.squareRoot(number);
    }

}
