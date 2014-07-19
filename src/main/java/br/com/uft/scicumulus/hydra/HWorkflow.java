package br.com.uft.scicumulus.hydra;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe que apresenta os dados de um workflow
 *
 * @author Eduardo, Vítor
 * @since 2010-12-25
 */
public class HWorkflow implements Serializable {

    public enum ExecModel {
        STA_FAF, DYN_FAF, STA_FTF, DYN_FTF
    }
    protected ExecModel model = ExecModel.DYN_FAF;
    protected Integer wkfId = null;
    protected String description;
    protected String tag;
    protected String exeTag;
    protected String expDir;
    protected String pmonitor;
    protected boolean verbose = false;
    protected ArrayList<HActivity> activities = new ArrayList<HActivity>();
    protected ArrayList<HActivity> runnableActivities = new ArrayList<HActivity>();

    /**
     * Construtor de um workflow
     */
    public HWorkflow() {
    }

    /**
     * Realiza a interação de execução de cada atividade do workflow
     *
     * @return 
     */
    private void addPipelineTasks(ArrayList<HActivation> resultList, HActivity act, HActivation task) throws SQLException {
        HActivity dep = act.pipeline;
        while (dep != null) {
            HActivation deptask = new HActivation(dep);
            deptask.commandLine = deptask.act.activation;
            String folderTask = task.workspace.replace(task.act.tag, dep.tag);
            HydraUtils.createDirectory(deptask.act.workflow.expDir + deptask.act.tag);
            deptask.workspace = folderTask;
            HProvenance.storeActivation(deptask);
            resultList.add(deptask);
            deptask.pipelinedFrom = task;
            dep = dep.pipeline;
            task = deptask;
        }
    }

    public HActivation[] iterateExecution() {
        ArrayList<HActivation> resultList = new ArrayList<HActivation>();
        boolean returnWait = false;
        try {
            int i = 0;
            while ((runnableActivities.size() > 0) && (i < runnableActivities.size())) {
                HActivity act = runnableActivities.get(i);
                if (act.status == HActivity.StatusType.READY) {
                    act.startTime = new Date();
                    if (act.atype == HActivity.ActType.MAP || act.atype == HActivity.ActType.SPLIT_MAP) {
                        act.generateMapTasks();
                        HActivity dep = act.pipeline;
                        while (dep != null) {
                            dep.status = HActivity.StatusType.PIPELINED;
                            dep.startTime = new Date();
                            HProvenance.storeActivity(dep);
                            dep = dep.pipeline;
                        }
                    } else if ((act.atype == HActivity.ActType.SR_QUERY) || (act.atype == HActivity.ActType.JOIN_QUERY)) {
                        if (act.atype == HActivity.ActType.SR_QUERY) {
                            act.openQuery();
                        } else {
                            act.openJoinQuery();
                        }
                        act.endTime = new Date();
                    } else if (act.atype == HActivity.ActType.REDUCE) {
                        act.generateReduceTasks();
                    }
                }
                if (act.status == HActivity.StatusType.RUNNING) {
                    HActivation item = HProvenance.loadReadyTask(act);
                    if (item != null) {
                        int k = 1, n = act.numActivations / (Hydra.MPI_size * Hydra.numberOfThreads);
                        resultList.add(item);
                        addPipelineTasks(resultList, act, item);
                        if ((act.workflow.model == HWorkflow.ExecModel.STA_FAF) || (act.workflow.model == HWorkflow.ExecModel.STA_FTF)) {
                            while ((item != null) && (k < n)) {
                                item = HProvenance.loadReadyTask(act);
                                if (item != null) {
                                    resultList.add(item);
                                    if (act.workflow.model == HWorkflow.ExecModel.STA_FTF) {
                                        addPipelineTasks(resultList, act, item);
                                    }
                                    k++;
                                }
                            }
                        }
                    } else if (HProvenance.checkIfAllTasksFinished(act)) {
                        act.status = HActivity.StatusType.FINISHED;
                        act.endTime = new Date();
                        act.createOutputRelation();
                    } else {
                        returnWait = true;
                        /* Start - SBBD medir depois remover */
                        if (act.workflow.model == HWorkflow.ExecModel.STA_FAF)
                            break;
                        /* End - SBBD medir */
                    }
                }
                if (act.status == HActivity.StatusType.FINISHED) {
                    HProvenance.storeActivity(act);
                    HActivity dep = act.pipeline;
                    while (dep != null) {
                        dep.status = HActivity.StatusType.FINISHED;
                        dep.endTime = new Date();
                        HProvenance.storeActivity(dep);
                        dep.createOutputRelation();
                        dep = dep.pipeline;
                    }
                    runnableActivities.remove(act);
                    evaluateDependencies();
                    if (resultList.size() == 0) {
                        i = 0;
                        continue;
                    }
                }
                if (resultList.size() > 0) {
                    break;
                }
                i++;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if ((resultList.size() == 0) && returnWait) {
            resultList.add(HActivation.WAIT_TASK);
        }
        if (resultList.size() > 0) {
            HActivation[] result = new HActivation[resultList.size()];
            for (int i = 0; i < resultList.size(); i++) {
                result[i] = resultList.get(i);
            }
            return result;
        }
        return null;
    }

    /**
     * Obtêm a atividade a partir de sua tag
     *
     * @param actTag
     * @return
     */
    public HActivity getActivity(String actTag) {
        for (int i = 0; i < activities.size(); i++) {
            HActivity activity = activities.get(i);
            if (activity.tag.equals(actTag)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * Calcula a dependência entre as atividades do workflow
     */
    public void evaluateDependencies() {
        HActivity actDependent = null;

        for (int i = 0; i < activities.size(); i++) {
            HActivity activity = activities.get(i);
            if (activity.status == HActivity.StatusType.BLOCKED) {
                HActivity.StatusType newStatus = HActivity.StatusType.READY;
                for (int j = 0; j < activity.relations.size(); j++) {
                    HRelation relation = activity.relations.get(j);
                    if (relation.relDependency != null) {
                        actDependent = getActivity(relation.relDependency);
                        if (actDependent.status != HActivity.StatusType.FINISHED) {
                            newStatus = HActivity.StatusType.BLOCKED;
                            break;
                        }
                    }
                }
                activity.status = newStatus;
            }
        }

        for (int i = activities.size() - 1; i >= 0; i--) {
            HActivity activity = activities.get(i);
            if ((activity.status == HActivity.StatusType.READY) || (activity.status == HActivity.StatusType.RUNNING)) {
                if (runnableActivities.indexOf(activity) < 0) {
                    runnableActivities.add(activity);
                }
            }
        }
    }

    public void checkPipeline() {
        if ((model != ExecModel.DYN_FTF) && (model != ExecModel.STA_FTF)) {
            return;
        }
        for (int i = activities.size() - 1; i >= 0; i--) {
            HActivity activity = activities.get(i);
            if (activity.atype != HActivity.ActType.MAP) {
                continue;
            }
            HRelation relation = activity.relations.get(0);
            if (relation.relDependency != null) {
                HActivity actDependent = getActivity(relation.relDependency);
                if (actDependent.atype == HActivity.ActType.MAP) {
                    actDependent.pipeline = activity;
                }
            }
        }
    }
}
