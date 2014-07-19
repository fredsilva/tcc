package br.com.uft.scicumulus.hydra;

import br.com.uft.scicumulus.hydra.HActivity.StatusType;

/**
 * Classe respons�vel pela execu��o das tarefas
 *
 * @author Jonas, Eduardo
 * @since 2010-12-25
 */
public class HHead extends Thread {

    public HActivation[] task = null;
    public HActivation[] taskFinished = null;
    public StatusType status = StatusType.READY;
    public boolean constrained = false;
    public int core = 1;

    /**
     * Construtor da classe HHead
     */
    public HHead(int core) {
        super();
        this.core = core;
    }

    /**
     * M�todo que realiza a execu��o de uma tarefa, caso head n�o possua
     * tarefa e a tarefa esteja pronta para executar
     */
    @Override
    @SuppressWarnings({"SleepWhileHoldingLock", "CallToThreadDumpStack"})
    public void run() {
        while (status != StatusType.FINISHED) {
            if (!this.gotConstrained()) {
                if (status != StatusType.BLOCKED) {
                    //a Head fica bloaqueada enquanto seu constrained for TRUE mas as threads
                    //do body ainda estiverem ocupadas.
                    if ((task != null)) {
                        for (int i = 0; i < task.length; i++) {
                            if (task[i].status == StatusType.READY) {
                                task[i].status = HActivity.StatusType.RUNNING;
                                task[i].executeTask();
                            }
                        }
                        if (this.constrained) {
                            this.constrained = false;
                        }
                        taskFinished = task;
                        task = null;
                    } else {
                        HydraUtils.sleep();
                    }
                } else {
                    HydraUtils.sleep();
                }
            }


        }
    }

    private boolean gotConstrained() {
        if (!this.constrained && task != null) {
            for (HActivation t : task) {
                if (t.act.isConstrained()) {
                    this.constrained = true;
                    this.status = StatusType.BLOCKED;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isWaiting() {
        if (this.status.equals(StatusType.BLOCKED)) {
            return true;
        } else if (this.task == null && this.taskFinished == null) {
            return true;
        } else {
            return false;
        }
    }
}
