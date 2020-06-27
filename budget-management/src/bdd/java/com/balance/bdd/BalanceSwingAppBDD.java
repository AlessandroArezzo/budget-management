package com.balance.bdd;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features="src/bdd/resources",monochrome=true ,tags="not @skipped")
public class BalanceSwingAppBDD {
	
	@BeforeClass
	public static void setUpOnce() { 
		FailOnThreadViolationRepaintManager.install();
	}
	
}
