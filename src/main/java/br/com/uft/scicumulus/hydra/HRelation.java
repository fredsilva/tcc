package br.com.uft.scicumulus.hydra;

import java.io.Serializable;

/**
 *  Classe HRelation possui todas as propriedades de um relação
 *
 * @author Eduardo
 * @since 2010-12-25
 */
public class HRelation implements Serializable {
    protected HActivity act;
    protected Integer relID = null;    

    public enum RelType {
        INPUT, OUTPUT
    }

    protected RelType relType;
    protected String relName;
    protected String relFile;
    protected String relDependency;

    /**
     * Construtor da classe HRelation
     *
     * @param act
     */
    public HRelation(HActivity act) {
        this.act = act;
        if (act != null)
            act.relations.add(this);
    }    
}
