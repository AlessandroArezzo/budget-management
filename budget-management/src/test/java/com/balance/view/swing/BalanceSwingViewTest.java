package com.balance.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
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
			new Client("test identifier 1"); 
	private static final Client CLIENT_FIXTURE_2=
			new Client("test identifier 2"); 
	
	private static final Invoice INVOICE_FIXTURE_1=
			new Invoice(CLIENT_FIXTURE_1, new Date(), 10);
	
	private static final Invoice INVOICE_FIXTURE_2=
			new Invoice(CLIENT_FIXTURE_2, new Date(), 20);
	
	private static final int CURRENT_YEAR=Calendar.getInstance().get(Calendar.YEAR);
	
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
		window.comboBox("yearsCombobox");
	}
	
	@Test @GUITest
	public void testShowAllClientsShouldAddClientsDescriptionsToTheClientsList(){ 
		GuiActionRunner.execute(() -> 
			balanceSwingView.showClients(Arrays.asList(CLIENT_FIXTURE_1, CLIENT_FIXTURE_2)) 
		);
		String[] listContents = window.list("clientsList").contents(); 
		assertThat(listContents).containsExactly(CLIENT_FIXTURE_1.toString(), CLIENT_FIXTURE_2.toString());
	}
	
	@Test @GUITest
	public void testShowAllInvoicesShouldAddInvoicesDescriptionsToTheInvoicesList(){ 
		GuiActionRunner.execute(() -> 
			balanceSwingView.showInvoices(Arrays.asList(INVOICE_FIXTURE_1, INVOICE_FIXTURE_2)) 
		);
		String[] listContents = window.list("invoicesList").contents(); 
		assertThat(listContents).containsExactly(INVOICE_FIXTURE_1.toString(), INVOICE_FIXTURE_2.toString());
	}
	
	@Test @GUITest
	public void testShowAllInvoicesShouldAddInvoicesDescriptionsToTheInvoicesListAndResetPrevious(){ 
		GuiActionRunner.execute(() -> {
				balanceSwingView.getInvoiceListModel().add(0,INVOICE_FIXTURE_1);
				balanceSwingView.showInvoices(Arrays.asList(INVOICE_FIXTURE_2));
			}
		);
		String[] listContents = window.list("invoicesList").contents(); 
		assertThat(listContents).containsExactly(INVOICE_FIXTURE_2.toString());
	}
	
	@Test @GUITest
	public void testSetAnnualTotalRevenueWithoutNoZeroNotSignificant() {
		GuiActionRunner.execute(() -> 
				balanceSwingView.setAnnualTotalRevenue(2019,300.55)
			);
		window.label("revenueLabel").requireText(
					"Il ricavo totale del 2019 è di 300.55€");
	}
	
	@Test @GUITest
	public void testSetAnnualTotalRevenueWithOneOrMoreZeroNotSignificant() {
		GuiActionRunner.execute(() -> 
			balanceSwingView.setAnnualTotalRevenue(2019,300.50)
		);
		window.label("revenueLabel").requireText(
					"Il ricavo totale del 2019 è di 300.50€");
	}
	
	@Test 
	public void testSetYearSelectedInComboboxWhenYearIsInCombobox() {
		GuiActionRunner.execute(() -> {
				DefaultComboBoxModel<Integer> listYearsModel =balanceSwingView.getComboboxYearsModel();
				listYearsModel.addElement(CURRENT_YEAR-1);
				listYearsModel.addElement(CURRENT_YEAR);
				listYearsModel.setSelectedItem(CURRENT_YEAR);
				balanceSwingView.setYearSelected(CURRENT_YEAR-1);
			}
		);
		window.comboBox("yearsCombobox").requireSelection(Pattern.compile(""+(CURRENT_YEAR-1)));
	}
	@Test 
	public void testSetYearSelectedInComboboxWhenYearIsNotInCombobox() {
		GuiActionRunner.execute(() -> {
				DefaultComboBoxModel<Integer> listYearsModel =balanceSwingView.getComboboxYearsModel();
				listYearsModel.addElement(CURRENT_YEAR);
				listYearsModel.setSelectedItem(CURRENT_YEAR);
				balanceSwingView.setYearSelected(CURRENT_YEAR-1);
			}
		);
		window.comboBox("yearsCombobox").requireSelection(Pattern.compile(""+(CURRENT_YEAR)));
	}
	
	
	@Test @GUITest
	public void testSetChoiceYearInvoicesWhenThereIsCurrentYear() {
		GuiActionRunner.execute(() -> 
			balanceSwingView.setChoiceYearInvoices(Arrays.asList(CURRENT_YEAR-1,CURRENT_YEAR))
		);
		assertThat(window.comboBox("yearsCombobox").contents()).containsExactly(""+(CURRENT_YEAR-1),
				""+CURRENT_YEAR);
	}
	
	@Test @GUITest
	public void testSetChoiceYearInvoicesWhenThereIsNotCurrentYear() {
		GuiActionRunner.execute(() -> 
			balanceSwingView.setChoiceYearInvoices(Arrays.asList(CURRENT_YEAR-1))
		);
		assertThat(window.comboBox("yearsCombobox").contents()).containsExactly(""+(CURRENT_YEAR-1),
				""+CURRENT_YEAR);
	}
	
	@Test @GUITest
	public void testSelectYearShouldDelegateToControllerFindYearsInvoices() {
		GuiActionRunner.execute(() -> {
			DefaultComboBoxModel<Integer> listYearsModel =balanceSwingView.getComboboxYearsModel();
			listYearsModel.addElement(CURRENT_YEAR-1);
			listYearsModel.addElement(CURRENT_YEAR);
			}
		);
		window.comboBox("yearsCombobox").selectItem(0);
		verify(balanceController).allInvoicesByYear(CURRENT_YEAR-1);
		verify(balanceController).annualRevenue(CURRENT_YEAR-1);
	}
	
}
