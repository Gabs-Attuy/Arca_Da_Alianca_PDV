package util;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 *
 * @author jeff_
 */
public class Validator {
    
    public ArrayList<String> errorMessages = new ArrayList<>();
    
    public boolean hasErro() {

        return !this.errorMessages.isEmpty();
    }
    
    public String getMensagensErro() {

        String errosFormulario = "";

        for (String msg : this.errorMessages) {
            errosFormulario += msg + "\n";
        }

        if (!errosFormulario.equals("")) {
            this.limparMensagens();
        }

        return errosFormulario;
    }
    
    public void limparMensagens() {
        this.errorMessages.clear();
    }
    
    public void validateNullField(JTextField txt) {
        if (txt.getText().trim().equals("")) {
            this.errorMessages.add("O campo (" + txt.getName() + ") não pode ser vazio!");
            txt.setBorder(BorderFactory.createLineBorder(Color.red));
        } else {
            txt.setBorder(UIManager.getBorder("TextField.border"));
        }
    }
    
    public void validatePrice(JTextField txt) {
         try {
            if (txt.getText().trim().equals("")) {
                throw new IllegalArgumentException();
            }

            Float.valueOf(txt.getText().replace(",", "."));
            txt.setBackground(Color.WHITE);

        } catch (NumberFormatException e) {
            this.errorMessages.add("Falha ao converter o valor do campo " + txt.getName() + " em float");
            txt.setBorder(BorderFactory.createLineBorder(Color.red));
            
        } catch (IllegalArgumentException e) {
            this.errorMessages.add("O campo (" + txt.getName() + ") não poder ser vazio!");
            txt.setBorder(BorderFactory.createLineBorder(Color.red));
        }
    }
    
    public void onlyNumber(KeyEvent evt) {
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }
    
    public void validateComboBox(JComboBox<?> comboBox) {
        Object item = comboBox.getSelectedItem();
        
        if (item.toString().equalsIgnoreCase("- Selecione uma opção -")) {
            this.errorMessages.add("Selecione uma opção válida para o campo (" + comboBox.getName() + ").");
            comboBox.setBorder(BorderFactory.createLineBorder(Color.red));
        } else
            comboBox.setBorder(UIManager.getBorder("ComboBox.border"));
    }
}
