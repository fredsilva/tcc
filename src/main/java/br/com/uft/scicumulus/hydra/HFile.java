package br.com.uft.scicumulus.hydra;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe que apresenta as propriedades de um arquivo
 *
 * @author Eduardo
 * @since 2010-12-25
 */
public class HFile implements Serializable {

    protected Integer fileID = null;
    protected HActivity act;
    protected HActivation task;
    protected boolean template = false;
    protected boolean instrumented = false;
    protected String fileDir = null;
    protected String fileName = null;
    protected Integer fileSize = null;
    protected Date fileData = null;

    /**
     * Construtor da classe HFile. Se os valores da atividade ou da tarefa
     * for diferente de null, adiciona nesses elementos o arquivo
     *
     * @param act
     * @param task
     */
    public HFile(HActivity act, HActivation task) {
        this.act = act;
        this.task = task;
        if (task != null) {
            task.files.add(this);
        } else if (act != null) {
            // activity only stores array of activity only files (templates, relations etc)
            act.files.add(this);
        }
    }    
}
