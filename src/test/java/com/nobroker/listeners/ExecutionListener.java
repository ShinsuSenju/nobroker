package com.nobroker.listeners;

import org.testng.IExecutionListener;

public class ExecutionListener implements IExecutionListener {

	@Override
	public void onExecutionStart() {
		// TODO Auto-generated method stub
		System.out.println("Execution Started");
		IExecutionListener.super.onExecutionStart();
	}

	@Override
	public void onExecutionFinish() {
		// TODO Auto-generated method stub
		System.out.println("Execution Ended!");
		IExecutionListener.super.onExecutionFinish();
	}

}
