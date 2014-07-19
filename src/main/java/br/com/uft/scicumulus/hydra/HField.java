package br.com.uft.scicumulus.hydra;

import java.io.Serializable;

/**
 * Classe que possui os campos utilizados numa determinada relação
 *
 * @author Eduardo
 * @since 2010-12-25
 */
public class HField implements Serializable {

    protected HActivity act;
    protected Integer fieldID = null;
    protected String name;
    protected int decimalplaces = -1;

    public enum FieldType {
        FLOAT, STRING, FILE
    };
    
    protected FieldType ftype;

    public enum FileOper {
        MOVE, MOVE_DELETE, COPY, COPY_DELETE
    };

    protected FileOper operation;

    protected String relation_input;
    protected String relation_output;


    /**
     * Construtor da classe HField. Se a atividade que possui o HField for diferente
     * de null, ele adiciona o campo na atividade informada.
     *
     * @param act
     */
    public HField(HActivity act) {
        this.act = act;
        if (act != null) {
            act.fields.add(this);
        }
    }
}
