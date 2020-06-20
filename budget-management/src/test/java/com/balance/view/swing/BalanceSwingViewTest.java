package com.balance.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Date;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.balance.controller.BalanceController;
import com.balance.model.Client;
import com.balance.model.Invoice;

@RunWith(GUITestRunner.class)
public class BalanceSwingViewTest extends AssertJSwingJUnitTestCase{
	
	private FrameFixture window;
	
	private BalanceSwingView balanceSwingView;
	
	@Mock
	private BalanceController balanceController;
	
	private static final Client CLIENT_FIXTURE_1=
			new Client(new ObjectId().toString(), "test identifier 1"); 
	private static final Client CLIENT_FIXTURE_2=
			new Client(new ObjectId().toString(), "test identifier 2"); 
	
	private static final Invoice INVOICE_FIXTURE_1=
			new Invoice(new ObjectId().toString(), CLIENT_FIXTURE_1, new Date(), 10);
	
	private static final Invoice INVOICE_FIXTURE_2=
			new Invoice(new ObjectId().toString(), CLIENT_FIXTURE_2, new Date(), 20);
	
	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);
		GuiActionRunner.execute(() -> {
			balanceSwingView = new BalanceSwingView();
			balanceSwingView.setBalanceController(balanceController);
			return balanceSwingView; 
		});
		window = new FrameFixture(robot(), balanceSwingView);
		window.show();
	}
	
	@Test @GUITest
	public void testControlInitialStates() {
		window.label(JLabelMatcher.withText("CLIENTI")); 
		window.label(JLabelMatcher.withText("GESTIONE RICAVI")); 
		window.label(JLabelMatcher.withText("FATTURE"));
		window.list("clientsList");
		window.list("invoicesList");
		window.label("revenueLabel");
	}
	
	@Test @GUITest
	public void testsShowAllClientsShouldAddClientsDescriptionsToTheClientsList(){ 
		GuiActionRunner.execute(() -> 
			balanceSwingView.showClients(Arrays.asList(CLIENT_FIXTURE_1, CLIENT_FIXTURE_2)) 
		);
		String[] listContents = window.list("clientsList").contents(); 
		assertThat(listContents).containsExactly(CLIENT_FIXTURE_1.toString(), CLIENT_FIXTURE_2.toString());
	}
	
	@Test @GUITest
	public void testsShowAllInvoicesShouldAddInvoicesDescriptionsToTheInvoicesList(){ 
		
		GuiActionRunner.execute(() -> 
			balanceSwingView.showInvoices(Arrays.asList(INVOICE_FIXTURE_1, INVOICE_FIXTURE_2)) 
		);
		String[] listContents = window.list("invoicesList").contents(); 
		assertThat(listContents).containsExactly(INVOICE_FIXTURE_1.toString(), INVOICE_FIXTURE_2.toString());
	}
	
	@Test @GUITest
	public void setAnnualTotalRevenueWithoutNoZeroNotSignificant() {
		GuiActionRunner.execute(() -> 
				balanceSwingView.setAnnualTotalRevenue(2019,300.55)
			);
		window.label("revenueLabel").requireText(
					"Il ricavo totale del 2019 è di 300.55€");
	}
	
	@Test @GUITest
	public void setAnnualTotalRevenueWithOneOrMoreZeroNotSignificant() {
		GuiActionRunner.execute(() -> 
			balanceSwingView.setAnnualTotalRevenue(2019,300.50)
		);
		window.label("revenueLabel").requireText(
					"Il ricavo totale del 2019 è di 300.50€");
	}
	
}
