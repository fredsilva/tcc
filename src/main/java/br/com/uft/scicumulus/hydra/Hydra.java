package br.com.uft.scicumulus.hydra;

import java.io.*;
import java.sql.SQLException;
import mpi.MPI;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

//Executar como single instance com x threads: x exp/hydra.xml
//Executar com MPJ no no y com x threads; y dir/mpj.conf niodev MPI x exp/hydra.xml
/**
 * Classe principal do Hydra
 *
 * @author Eduardo, Jonas, Vítor, Daniel
 * @version 0.8
 * @since 2011-2-25
 */
public class Hydra {

    //MPI Attributes
    private boolean isMPI = false;
    private static int MPI_rank = 0;
    protected static int MPI_size = 1;
    protected static int numberOfThreads = 0;
    private HConfiguration config;
    private HWorkflow workflow;
    private Thread listenerThread;
    private HListener listener = null;
    private HBody hydraBody;
    protected static boolean mainNode = false;

    /**
     * Método principal do Hydra
     *
     * @param args argumentos para execução do Hydra
     * @return void
     */
    @SuppressWarnings({"CallToThreadDumpStack", "static-access"})
    public static void main(String[] args) {

        Hydra hydra = new Hydra();
        try {
            System.out.println("Initializing SciCumulus Core...");

            System.out.println("Preparing Parallel Execution...");
            hydra.prepare(args);

            System.out.println("Open Workflow Configuration...");
            if (hydra.mainNode) {
                hydra.open();
            }
            System.out.println("Executing Workflow...");
            hydra.execute();
            System.out.println("Closing SciCumulus...");
            hydra.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Prepara para executar o Hydra
     *
     * @param args
     * @return void
     * @throws ParsingException
     * @throws ValidityException
     * @throws IOException
     * @throws SQLException
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    private void prepare(String[] args) throws ParsingException, ValidityException, IOException, SQLException, Exception {
        //Set up arguments
        int j = -1;
        //discover wich argument is the MPI argument
        for (int i = 0; i < args.length; i++) {
            if (args[i].compareTo("MPI") == 0) {
                j = i;
            }
        }

        //if MPI is passed as argument ... setup MPI
        if (j >= 0) {
            MPI.Init(args);
            this.MPI_size = MPI.COMM_WORLD.Size();
            this.MPI_rank = MPI.COMM_WORLD.Rank();
            this.isMPI = true;
        } else {
            this.MPI_size = 1;
        }
        j++;

        if (!this.isMPI || this.MPI_rank == 0) {
            Hydra.mainNode = true;
        }
        String configurationFile;
        if (j + 1 < args.length) {
            // primeiro argumento ... number of threads
            this.numberOfThreads = Integer.parseInt(args[j]);
            // segundo argumento ... config file
            configurationFile = args[j + 1];
            config = new HConfiguration(configurationFile);
            workflow = config.readXMLConfiguration(this);

        } else if (j + 1 == args.length) {
            //se não foi passado o atributo número de threads
            this.numberOfThreads = Runtime.getRuntime().availableProcessors();
            //argumento q falta ... config file
            configurationFile = args[j];
            config = new HConfiguration(configurationFile);
            workflow = config.readXMLConfiguration(this);

        } else {
            throw new Exception("Unable to locate the configuration file. Check SciCumulus arguments.");
        }

    }

    /**
     * Open
     *
     * @return void
     * @throws Exception
     */
    public void open() throws Exception {
       
        
        File direct = new File(workflow.expDir);
        if ((!direct.exists()) && (workflow.pmonitor.equals("false"))) {
            direct.mkdirs();
        }        
    
        // Daniel
        
        direct = null;
        int tmpWkfId = HProvenance.matchWorkflow(workflow.tag, workflow.exeTag);
        if (tmpWkfId >= 0) {
            workflow.wkfId = tmpWkfId;
            HProvenance.matchActivities(workflow);
            workflow.evaluateDependencies();
        }
        HProvenance.storeWorkflow(workflow);
        for (int i = 0; i < workflow.activities.size(); i++) {
            HActivity act = workflow.activities.get(i);
            HProvenance.storeActivity(act);
        }
    }

    /**
     * Executa o Hydra
     *
     * @return void
     * @throws IOException
     * @throws InterruptedException
     * @throws SQLException
     * @throws Exception
     */
    public void execute() throws IOException, InterruptedException, SQLException, Exception {
        hydraBody = new HBody(MPI_rank, numberOfThreads);

        //start Listener if is MPI and in main node
        if ((MPI_size > 1) && MPI_rank == 0) {
            listener = new HListener(hydraBody, MPI_size);
            listenerThread = new Thread(listener);
            listenerThread.start();
        }

        //initializes on main node
        if (Hydra.mainNode) {
            hydraBody.workflow = workflow;
        }

        hydraBody.execute();

        if ((MPI_size > 1) && (MPI_rank == 0)) {
            while (listener.nodes > 0) {
                HydraUtils.sleep();
            }
        }
    }

    /**
     * Close
     *
     * @return void
     */
    private void close() {
        if (isMPI) {
            MPI.Finalize();
        }
    }
}
