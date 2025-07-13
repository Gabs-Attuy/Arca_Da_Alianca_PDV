package service;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * Testes unitários para a classe {@link ValidatorService}.
 *
 * <p>Esses testes verificam a validação de campos de texto, campos numéricos,
 * caixas de seleção e entrada de código de barras.
 *
 * @author jeff_
 */
public class ValidatorServiceTest {
    
    private ValidatorService validator;
    
    @BeforeEach
    void setUp() {
        validator = new ValidatorService();
    }
    
    /**
    * Verifica se um campo de texto vazio é corretamente identificado como inválido no método {@code validateNullField}.
    */
    @Test
    void testValidateNullFieldWithEmptyField() {
        JTextField txt = new JTextField("");
        txt.setName("Nome");
        
        validator.validateNullField(txt);
        
        assertTrue(validator.hasErro());
        assertTrue(validator.getMensagensErro().contains("não pode ser vazio"));
    }
    
    /**
    * Verifica se um campo de texto preenchido é corretamente identificado como válido no método {@code validateNullField}.
    */
    @Test
    void testValidateNullFieldWithNonEmptyField() {
        JTextField txt = new JTextField("valor");
        txt.setName("Campo");

        validator.validateNullField(txt);

        assertFalse(validator.hasErro());
    }
    
    /**
    * Verifica se um valor numérico válido é aceito pela validação de preço do método {@code validatePrice}.
    */
    @Test
    void testValidatePriceWithValidPrice() {
        JTextField txt = new JTextField("12.50");
        txt.setName("Preço");
        
        validator.validatePrice(txt);
        
        assertFalse(validator.hasErro());
    }
    
    /**
    * Verifica se um valor numérico inválido é reprovado pela validação de preço do método {@code validatePrice}.
    */
    @Test
    void testValidatePriceWithInvalidPrice() {
        JTextField txt = new JTextField("abc");
        txt.setName("Preço");
        
        validator.validatePrice(txt);
        
        assertTrue(validator.hasErro());
        assertTrue(validator.getMensagensErro().contains("Falha ao converter"));
    }
    
    /**
    * Verifica se a ComboBox com a opção inválida é reprovada na validação do método {@code validateComboBox}.
    */
    @Test
    void testValidateComboBoxWithInvalidOption() {
        JComboBox<String> comboBox = new JComboBox<>(new String[] {
            "- Selecione uma opção -",
            "Opção 1"
        });
        comboBox.setSelectedIndex(0);
        comboBox.setName("Categoria");
        
        validator.validateComboBox(comboBox);
        
        assertTrue(validator.hasErro());
        assertTrue(validator.getMensagensErro().contains("Selecione uma opção válida"));
    }
    
    /**
    * Verifica se a ComboBox com a opção válida é aceita na validação do método {@code validateComboBox}.
    */
    @Test
    void testValidateComboBox_withValidOption() {
        JComboBox<String> combo = new JComboBox<>(new String[]{"- Selecione uma opção -", "Opção 1"});
        combo.setSelectedIndex(1);
        combo.setName("Categoria");

        validator.validateComboBox(combo);

        assertFalse(validator.hasErro());
    }
}
