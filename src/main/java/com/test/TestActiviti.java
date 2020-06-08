package com.test;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试activiti
 *
 * @author hxh
 * @version 1.0.0
 * @date 2020-06-05
 */
public class TestActiviti {

	private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/**
	 * 流程KEY名称
	 */
	public static final String HOLIDAY = "holiday";

	public static final String BUSINESS_ID = "test01";

	/**
	 * 测试部署
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testDeployment() throws FileNotFoundException {
		InputStream inputStreamBpmn = this.getClass().getClassLoader().getResourceAsStream("holiday.bpmn");
		InputStream inputStreamPng = this.getClass().getClassLoader().getResourceAsStream("holiday.png");
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		Deployment deploy = processEngine.getRepositoryService().createDeployment()
				.addInputStream("holiday.bpmn", inputStreamBpmn).addInputStream("holiday.png", inputStreamPng).deploy();
		System.out.println("流程部署ID" + deploy.getId());
	}

	/**
	 * 测试删除部署
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testDeleteDeployment() {
		RepositoryService repositoryService = processEngine.getRepositoryService();

		DeploymentQuery holiday = repositoryService.createDeploymentQuery().processDefinitionKey(HOLIDAY);
		Deployment deployment = holiday.singleResult();
		System.out.println(deployment.getId());

		repositoryService.deleteDeployment(deployment.getId());
	}

	/**
	 * 测试查询流程信息
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testQueryDeployment() {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		DeploymentQuery holiday = repositoryService.createDeploymentQuery().processDefinitionKey(HOLIDAY);
		Deployment deployment = holiday.singleResult();
		System.out.println(deployment.getId());
	}

	/**
	 * 测试启动流程实例
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testStartProcessInstance() {
		RuntimeService runtimeService = processEngine.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(HOLIDAY);

		System.out.println(processInstance.getId());
		System.out.println(processInstance.getId());
		System.out.println(processInstance.getName());
		System.out.println(processInstance.getDeploymentId());
		System.out.println(processInstance.getActivityId());
	}

	/**
	 * 测试开始流程实例 业务id
	 * 
	 * @return
	 * @author hxh
	 */
	@Test
	public void testStartProcessInstanceWithBusinessId() {
		RuntimeService runtimeService = processEngine.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(HOLIDAY, BUSINESS_ID);
		System.out.println(processInstance.getId());
		System.out.println(processInstance.getName());
		System.out.println(processInstance.getBusinessKey());

		runtimeService.deleteProcessInstance(processInstance.getId(),"ddddfdsafdsafd");
	}

	/**
	 * 测试查询流程实例
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testQueryProcessInstance() {
		RuntimeService runtimeService = processEngine.getRuntimeService();
		List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery()
				.processDefinitionKey(HOLIDAY).list();
		if (processInstanceList == null || processInstanceList.isEmpty()) {
			System.out.println("无运行中的流程实例！");
		} else {
			for (ProcessInstance processInstance : processInstanceList) {
				System.out.println("======================================");
				System.out.println(processInstance.getProcessInstanceId());
				System.out.println(processInstance.getDeploymentId());
				System.out.println(processInstance.getProcessDefinitionId());
				System.out.println(processInstance.getBusinessKey());
			}
		}
	}

	/**
	 * 测试暂停或激活的定义
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testSuspendOrActivateDefinition() {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(HOLIDAY).list();
		for (ProcessDefinition processDefinition : processDefinitionList) {
			boolean suspend = processDefinition.isSuspended();
			if (suspend) {
				repositoryService.activateProcessDefinitionById(processDefinition.getId(), true, null);
				System.out.println("激活流程实例" + processDefinition.getId());
			} else {
				repositoryService.suspendProcessDefinitionById(processDefinition.getId(), true, null);
				System.out.println("挂起流程" + processDefinition.getId());
			}

		}

	}

	/**
	 * 测试开始与业务流程实例id和分配用户
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testStartProcessInstanceWithBusinessIdAndAssigned() {
		RuntimeService runtimeService = processEngine.getRuntimeService();

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("BMJL", "wuangwu");

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(HOLIDAY, BUSINESS_ID, variables);

		System.out.println(processInstance.getId());
		System.out.println(processInstance.getName());
		System.out.println(processInstance.getBusinessKey());
	}

	/**
	 * 测试查询任务
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testQueryTask() {
		TaskService taskService = processEngine.getTaskService();
		getTask(taskService);
	}

	/**
	 * 查询所有任务或者是个人任务
	 *
	 * @param taskService
	 *            任务服务
	 * @return {@link List<Task> }
	 * @author hxh
	 */
	private List<Task> getTask(TaskService taskService) {
		List<Task> taskList = taskService.createTaskQuery().processDefinitionKey(HOLIDAY)
				.processInstanceBusinessKey(BUSINESS_ID)
				// .taskAssignee("")// 根据任务责任人查询
				.list();
		if (taskList == null || taskList.isEmpty()) {
			System.out.println("任务信息为空");

		} else {
			for (Task task : taskList) {
				System.out.println("查询到任务" + task.getId() + ",task_name" + task.getName() + ",Assignee"
						+ task.getAssignee() + ",ACT_TYPE=" + task.getTaskDefinitionKey());
			}
		}
		return taskList;
	}

	/**
	 * 测试完成任务
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testCompleteTask() {
		TaskService taskService = processEngine.getTaskService();
		List<Task> taskList = getTask(taskService);

		for (Task task : taskList) {
			// 处理任务设置变量
			Map<String, Object> variables = new HashMap<>(1);
			variables.put("days", 4);
			variables.put("BMJL", "bmjl");
			taskService.complete(task.getId(), variables);
			System.out.println("编号" + task.getId() + "任务完成");
		}

	}

	@Test
	public void testMultipleAssign() {
		TaskService taskService = processEngine.getTaskService();
		List<Task> taskList = taskService.createTaskQuery().processDefinitionKey(HOLIDAY).taskCandidateUser("hameimei")
				.list();
		if (taskList == null || taskList.isEmpty()) {
			System.out.println("任务信息为空");

		} else {
			for (Task task : taskList) {
				System.out.println("查询到任务" + task.getId() + ",task_name" + task.getName() + ",Assignee"
						+ task.getAssignee() + ",ACT_TYPE=" + task.getTaskDefinitionKey());
			}
		}
	}

	/**
	 * 测试组的任务
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testGroupTask() {
		TaskService taskService = processEngine.getTaskService();
		List<Task> taskList = taskService.createTaskQuery().processDefinitionKey(HOLIDAY).taskCandidateGroup("ztgroup")
				.list();
		if (taskList == null || taskList.isEmpty()) {
			System.out.println("任务信息为空");

		} else {
			for (Task task : taskList) {
				System.out.println("查询到任务" + task.getId() + ",task_name" + task.getName() + ",Assignee"
						+ task.getAssignee() + ",ACT_TYPE=" + task.getTaskDefinitionKey());
			}
		}
	}

	/**
	 * 测试拾取任务
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testClaimTask() {
		TaskService taskService = processEngine.getTaskService();
		List<Task> taskList = taskService.createTaskQuery().processDefinitionKey(HOLIDAY)
//				.taskCandidateUser("hameimei")
				.taskCandidateGroup("ztgroup")
				.list();
		if (taskList != null && !taskList.isEmpty()) {
			for (Task task : taskList) {
				taskService.claim(task.getId(), "hameimei");
			}
		} else {
			System.out.println("没有相关任务！");
		}
	}

	/**
	 * 测试  归还组任务
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testSendBackTack() {
		TaskService taskService = processEngine.getTaskService();

		List<Task> taskList = taskService.createTaskQuery().processDefinitionKey(HOLIDAY).taskAssignee("bmjl").list();
		if (CollectionUtils.isEmpty(taskList)) {
			System.out.println("没有相关任务信息");
			return;
		}
		for (Task task : taskList) {
			taskService.setAssignee(task.getId(), null);
			System.out.println(task.getId() + "归还任务组");
		}
	}
	/**
	 * 测试 任务交接
	 *
	 * @return
	 * @author hxh
	 */
	@Test
	public void testSetAssigneeToCandidateUser() {
		TaskService taskService = processEngine.getTaskService();

		List<Task> taskList = taskService.createTaskQuery().processDefinitionKey(HOLIDAY).
//				taskAssignee("bmjl").
				list();
		if (CollectionUtils.isEmpty(taskList)) {
			System.out.println("没有相关任务信息");
			return;
		}
		for (Task task : taskList) {
			taskService.setAssignee(task.getId(), "leilei");
			System.out.println(task.getId() + "已交给新用户： 蕾蕾");
		}
	}

}
