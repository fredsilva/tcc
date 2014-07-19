package br.com.uft.scicumulus.hydra;

import java.io.Serializable;

/**
 * A classe HMessage possui as informações da mensagem a ser enviada
 *
 * @author Jonas, Eduardo
 * @since 2010-12-25
 */
public class HMessage implements Serializable {

    public enum Type {
        REQUEST_TASK, STORE, PROCESS_TASK, WAIT, FINISH
    }
    
    protected Type type = Type.REQUEST_TASK;
    protected HActivation [] activation = null;
    protected int MPI_rank = 0;
}
