package com.nobroker.listeners;

import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class MethodListener implements IInvokedMethodListener {

	@Override
	public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
		// TODO Auto-generated method stub
		System.out.println("Before Method Invoked -"+
		method.getTestMethod().getMethodName());
		IInvokedMethodListener.super.beforeInvocation(method, testResult);
	}

	@Override
	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		// TODO Auto-generated method stub
		System.out.println("After Method Invoked -"+
				method.getTestMethod().getMethodName());
		IInvokedMethodListener.super.afterInvocation(method, testResult);
	}

}
