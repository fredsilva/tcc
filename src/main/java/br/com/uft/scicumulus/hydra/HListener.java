package br.com.uft.scicumulus.hydra;

import mpi.MPI;

/**
 * Classe HListener. O Listener executa no nó 0 (MPI_rank=0) apenas no modo de 
 * execuçãoo MPI. Ele recebe o pedido de outros nós e envia as tarefas para os 
 * mesmo. Quando um nó envia um pedido com a tarefa, significa que a tarefa for 
 * processada, portanto o Listener pode armazenar isso na base de dados
 *
 * @author Jonas, Eduardo
 * @since 2010-12-25
 */
public class HListener implements Runnable {

    public int nodes = 0;
    private HBody body;

    /**
     * Construtor da classe HListener.
     *
     * @param body
     * @param MPI_size
     */
    public HListener(HBody body, int MPI_size) {
        this.nodes = MPI_size - 1;
        this.body = body;
    }

    /**
     * Executa o Listener para receber e responder aos pedidos
     */
    @Override
    @SuppressWarnings("CallToThreadDumpStack")
    public void run() {
        while (nodes > 0) {
            //wait for incoming requests
            try {
                //MPI Recv buffer
                HMessage request[] = new HMessage[1];
                //if there are other HydraBodies on other MPI processes asking for tasks
                MPI.COMM_WORLD.Recv(request, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, 0);
                HMessage received = request[0];
                if (received != null) {
                    if (received.type == HMessage.Type.FINISH) {
                        nodes--;
                    } else {
                        int destiny = received.MPI_rank;
                        //if the message contains a processed task:
                        HMessage message = body.answerRequest(received);
                        //Message buffer for MPI send
                        HMessage answer[] = new HMessage[1];
                        answer[0] = message;
                        MPI.COMM_WORLD.Send(answer, 0, 1, MPI.OBJECT, destiny, 0);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
    }
}
