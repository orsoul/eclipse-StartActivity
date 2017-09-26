package com.fanfull.entity;

/**
 * 
 * @ClassName: BaseOperation
 * @Description: 封装基本操作 封袋，出库，入库，验封，开袋
 * @author Keung
 * @date 2015-3-13 下午03:25:11
 * 
 */
public class OperationBean {

	private int type;// 操作类型
	private String typeName;//
	private int conTask;// 选择任务 通讯编号
	private int conPi;// 上传数据 通讯编号
	
	
	private String finishNumber;//总完成数量
	private String pfinishNumber;//个人完成数量
	private String planNumber;//计划数量
	
	private int stateOperation=0;//当前操作的状态
	
	public OperationBean(int type) {
		super();
		this.type = type;
		switch (type) {
		case 0:
			conTask = 22;
			conPi = 23;
			typeName="封袋操作";
			break;
		case 1:
			conTask = 5;
			conPi = 6;
			typeName="入库操作";
			break;
		case 2:
			conTask = 32;
			conPi = 33;
			typeName="出库操作";
			break;
		case 3:
			conTask = 42;
			conPi = 43;
			typeName="验封操作";
			break;
		case 4:
			conTask = 52;
			conPi = 53;
			typeName="开袋操作";
			break;
		case 5:
			conTask = 22;
			conPi = 23;
			typeName="换袋操作";
			break;
		default:
			break;
		}
		
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public int getConTask() {
		return conTask;
	}

	public void setConTask(int conTask) {
		this.conTask = conTask;
	}

	public int getConPi() {
		return conPi;
	}

	public void setConPi(int conPi) {
		this.conPi = conPi;
	}
	
	public String getFinishNumber() {
		return finishNumber;
	}

	public void setFinishNumber(String finishNumber) {
		this.finishNumber = finishNumber;
	}

	public String getPfinishNumber() {
		return pfinishNumber;
	}

	public void setPfinishNumber(String pfinishNumber) {
		this.pfinishNumber = pfinishNumber;
	}

	public String getPlanNumber() {
		return planNumber;
	}

	public void setPlanNumber(String planNumber) {
		this.planNumber = planNumber;
	}
	
	public int getStateOperation() {
		return stateOperation;
	}

	public void setStateOperation(int stateOperation) {
		this.stateOperation = stateOperation;
	}

	@Override
	public String toString() {
		return "BaseOperation [type=" + type + ", typeName=" + typeName
				+ ", conTask=" + conTask + ", conPi=" + conPi
				+ ", finishNumber=" + finishNumber + ", pfinishNumber="
				+ pfinishNumber + ", planNumber=" + planNumber + "]";
	}

}
