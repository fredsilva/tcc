package br.com.uft.scicumulus.hydra;

import java.io.File;
import java.io.Serializable;

/**
 * Classe HValue apresenta as propriedades do valor de um campo
 *
 * @author Vítor, Eduardo
 * @since 2010-12-25
 */
public class HValue implements Serializable {

    public enum ValueType {

        INPUT, OUTPUT
    }
    protected ValueType status = null;
    protected HField field;
    protected HActivity act;
    protected HActivation task;
    protected int rowNumber;
    protected String stringValue = null;
    protected Double floatValue = null;
    protected String fileDir = null;
    protected String fileName = null;

    /**
     * Construtor da classe HValue
     *
     * @param field
     * @param act
     * @param task
     */
    public HValue(HField field, HActivity act, HActivation task, ValueType tytpe) {

        this.field = field;
        this.act = act;
        this.task = task;
        if (task != null) {
            if (tytpe == ValueType.INPUT) {
                task.iValues.add(this);
            }
            if (tytpe == ValueType.OUTPUT) {
                task.oValues.add(this);
            }
        }
    }

    public void copyValues(HValue val) {
        this.rowNumber = val.rowNumber;
        this.stringValue = val.stringValue;
        this.floatValue = val.floatValue;
        this.fileDir = val.fileDir;
        this.fileName = val.fileName;
    }

    /**
     * Obt�m os valores dos campos no formato String, independentemente do seu
     * tipo (FILE, FLOAT, STRING)
     *
     * @return
     */
    public String getValueAsString() {
        switch (field.ftype) {
            case FILE:
                return ((fileDir != null) && (fileName != null)) ? fileDir + fileName : "";
            case STRING:
                return (stringValue != null) ? String.valueOf(stringValue) : "";
            case FLOAT:
                String value = HydraUtils.formatFloat(floatValue, field.decimalplaces);
                return (value != null) ? value : "";
        }
        return "";
    }

    /**
     * Seta o valor de uma String em qualquer tipo de HValue
     *
     * @param value
     */
    public void setValueFromString(String value) {
        switch (field.ftype) {
            case FILE:
                File f = new File(value);
                String name = HydraUtils.getFileName(f);
                if (name.compareTo(value) != 0) {
                    fileDir = f.getParent().replace("\\", "/") + HydraUtils.SEPARATOR;
                    fileName = name;
                } else {
                    fileDir = act.workflow.expDir + "input" + HydraUtils.SEPARATOR;
                    fileName = value;
                }
                f = null;
                break;
            case STRING:
                this.stringValue = value;
                break;
            case FLOAT:
                this.floatValue = Double.valueOf(value);
                break;
        }

    }
}
