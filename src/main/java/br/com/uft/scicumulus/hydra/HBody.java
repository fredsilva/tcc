package br.com.uft.scicumulus.hydra;

import br.com.uft.scicumulus.hydra.HActivity.StatusType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mpi.MPI;

/**
 * Essa classe controla as threads que processam as tarefas e alimentam-nas com novas tarefas
 *
 * @author Jonas, Eduardo, Vítor
 * @since 2011-01-13
 */
public class HBody {

    private int MPI_rank;
    private List<HHead> heads;
    private int threads;
    protected HWorkflow workflow;
    private HHead constrained = null;
    private int blocked = 0;

    /**
     * Construtor da classe HBody
     *
     * @param MPI_rank
     * @param threads
     */
    public HBody(int MPI_rank, int threads) {
        this.MPI_rank = MPI_rank;
        heads = new ArrayList<HHead>();
        this.threads = threads;
    }

    /**
     * Esse método inicia as threads e as mantêm alimentadas com tarefas até que todas as tarefas sejam finalizadas
     *
     * @throws InterruptedException
     * @throws SQLException
     */
    @SuppressWarnings("SleepWhileHoldingLock")
    public void execute() throws InterruptedException, SQLException, Exception {
        //create the threads
        for (int i = 0; i < threads; i++) {
            HHead head = new HHead(i+1);
            heads.add(head);
            head.start();
        }

        //do it until you have threads available on your list of threads
        boolean receivedWait = false;
        int WaitCounter = 0;
        int WaitThreshold = 5;
        while (heads.size() > 0) {
            Iterator<HHead> iter = heads.iterator();
            while (iter.hasNext()) {
                HHead head = iter.next();
                if (this.constrained == null) {
                    if (head.constrained == true) {
                        this.constrained = head;
                        continue;
                    }
                } else {
                    if (head.isWaiting() && this.constrained != head) {
                        head.status = StatusType.BLOCKED;
                        this.blocked++;
                    }
                    if (blocked == heads.size() - 1) {
                        //ok, good to go
                        this.constrained.status = StatusType.READY;
                        blocked = 0;
                    }
                }

                if ((head.task == null) && (!receivedWait || (head.taskFinished != null))) {
                    HMessage sendMsg = new HMessage();
                    sendMsg.type = HMessage.Type.REQUEST_TASK;
                    sendMsg.activation = head.taskFinished;
                    head.taskFinished = null;
                    sendMsg.MPI_rank = this.MPI_rank;

                    //se o body est� no modo constrained, mas a thread constrained j� terminou, liberar outras threads
                    if (this.constrained == head) {
                        this.constrained = null;
                        this.blocked = 0;
                        if (!aHeadIsConstrained()) {
                            for (HHead h : heads) {
                                if (!h.constrained) {
                                    h.status = StatusType.READY;
                                }
                            }
                        }

                    }
                    //se alguma head está como constrained, bloquear as threads que terminaram suas tarefas
                    if (this.constrained != null && head.status != StatusType.BLOCKED) {
                        head.status = StatusType.BLOCKED;
                        sendMsg.type = HMessage.Type.STORE;
                    }

                    HMessage recvMsg = this.sendRequest(sendMsg);
                    if (recvMsg.type.equals(HMessage.Type.PROCESS_TASK)) {
                        for (int i = 0; i < recvMsg.activation.length; i++)
                            recvMsg.activation[i].processor = this.MPI_rank*threads + head.core;
                        head.task = recvMsg.activation;
                    } else if (recvMsg.type.equals(HMessage.Type.FINISH)) {
                        head.status = HActivity.StatusType.FINISHED;
                        iter.remove();
                    } else {
                        head.task = null;
                    }
                    if (recvMsg.type.equals(HMessage.Type.WAIT)) {
                        receivedWait = true;
                    }
                }

            }
            HydraUtils.sleep();
            if (receivedWait) {
                WaitCounter++;
                if (WaitCounter > WaitThreshold) {
                    receivedWait = false;
                    WaitCounter = 0;
                }
            }
        }
        if (MPI_rank > 0) {
            HMessage sendMsg = new HMessage();
            sendMsg.type = HMessage.Type.FINISH;
            sendMsg.activation = null;
            this.sendRequest(sendMsg);
        }
    }

    /**
     * Método responsável pelo envio de um pedido
     *
     * @param sendMsg
     * @return
     * @throws SQLException
     * @throws Exception
     */
    private HMessage sendRequest(HMessage sendMsg) throws SQLException, Exception {
        HMessage recvMsg = null;
        //send request locally
        if (MPI_rank == 0) {
            recvMsg = answerRequest(sendMsg);
        } else {
            //ask the listener (Listener changes the status of the Tasks to 1 already)
            HMessage sendMsgArray[] = new HMessage[1];
            sendMsgArray[0] = sendMsg;
            MPI.COMM_WORLD.Send(sendMsgArray, 0, 1, MPI.OBJECT, 0, 0);

            if (sendMsg.type != HMessage.Type.FINISH) {
                HMessage recvMsgArray[] = new HMessage[1];
                MPI.COMM_WORLD.Recv(recvMsgArray, 0, 1, MPI.OBJECT, 0, 0);
                recvMsg = recvMsgArray[0];
            }
        }
        return recvMsg;
    }

    /**
     * Método responsável pela resposta do pedido
     *
     * @param questionMsg
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public synchronized HMessage answerRequest(HMessage questionMsg) throws SQLException, Exception {
        HMessage answerMsg = new HMessage();
        if (questionMsg.type.equals(HMessage.Type.REQUEST_TASK)) {
            if (questionMsg.activation != null) {
                HProvenance.storeTasks(questionMsg.activation);
            }
            HActivation[] task = workflow.iterateExecution();
            if (task != null) {
                if ((task[0] != HActivation.WAIT_TASK)) {
                    answerMsg.type = HMessage.Type.PROCESS_TASK;
                    answerMsg.activation = task;
                    answerMsg.MPI_rank = questionMsg.MPI_rank;
                } else if (task[0] == HActivation.WAIT_TASK) {
                    answerMsg.type = HMessage.Type.WAIT;
                }
            } else {
                answerMsg.type = HMessage.Type.FINISH;
            }
        } else if (questionMsg.type.equals(HMessage.Type.STORE)) {
            if (questionMsg.activation != null) {
                HProvenance.storeTasks(questionMsg.activation);
            }
            //answerMsg.type = HMessage.Type.WAIT;
            //answerMsg.task = null;
        }
        return answerMsg;
    }

    private boolean aHeadIsConstrained() {
        boolean constrain = false;
        for (HHead h : heads) {
            if (h.constrained) {
                constrain = true;
                this.constrained = h;
            }
        }
        return constrain;
    }
}
