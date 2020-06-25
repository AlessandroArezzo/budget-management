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
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
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
	private static final int YEAR_FIXTURE=2019;
	
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
		window.label("labelClientErrorMessage").requireText("");
		window.comboBox("clientsCombobox");
		window.button(JButtonMatcher.withText("Vedi tutte le fatture"))
			.requireNotVisible();
	}
	
	@Test @GUITest
	public void testShowAllClientsShouldAddClientsDescriptionsToTheClientsListAndCombobox(){ 
		GuiActionRunner.execute(() -> 
			balanceSwingView.showClients(Arrays.asList(CLIENT_FIXTURE_1, CLIENT_FIXTURE_2)) 
		);
		assertThat(window.list("clientsList").contents())
			.containsExactly(CLIENT_FIXTURE_1.toString(), CLIENT_FIXTURE_2.toString());
		assertThat(window.comboBox("clientsCombobox").contents())
			.containsExactly(CLIENT_FIXTURE_1.toString(), CLIENT_FIXTURE_2.toString());
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
	
	@Test @GUITest
	public void testSelectYearWhenAClientIsSelectedShouldDelegateToControllerFindClientInvoices() {
		GuiActionRunner.execute(() -> {
			DefaultComboBoxModel<Integer> listYearsModel=balanceSwingView.getComboboxYearsModel();
			listYearsModel.addElement(CURRENT_YEAR-1);
			listYearsModel.addElement(CURRENT_YEAR);
			DefaultListModel<Client> listClientsModel =balanceSwingView.getClientListModel();
			listClientsModel.addElement(CLIENT_FIXTURE_1);
			listClientsModel.addElement(CLIENT_FIXTURE_2);
			}
		);
		window.list("clientsList").selectItem(1);
		window.comboBox("yearsCombobox").selectItem(0);
		verify(balanceController).allInvoicesByClientAndYear(CLIENT_FIXTURE_2, CURRENT_YEAR-1);
		verify(balanceController).annualClientRevenue(CLIENT_FIXTURE_2, CURRENT_YEAR-1);
	}
	
	@Test
	public void testChangeClientSelectedShoulDelegateToControllerFindInvoicesAndRevenue(){ 
		GuiActionRunner.execute(() -> {
			DefaultListModel<Client> listClientsModel =balanceSwingView.getClientListModel();
			listClientsModel.addElement(CLIENT_FIXTURE_1);
			listClientsModel.addElement(CLIENT_FIXTURE_2);
			DefaultComboBoxModel<Integer> listYearsModel =balanceSwingView.getComboboxYearsModel();
			listYearsModel.addElement(CURRENT_YEAR-1);
			listYearsModel.addElement(CURRENT_YEAR);
			listYearsModel.setSelectedItem(CURRENT_YEAR-1);
			}
		);
		window.list("clientsList").selectItem(0);
		verify(balanceController).allInvoicesByClientAndYear(CLIENT_FIXTURE_1, CURRENT_YEAR-1);
		verify(balanceController).annualClientRevenue(CLIENT_FIXTURE_1, CURRENT_YEAR-1);
	}
	
	@Test @GUITest
	public void testSetClientAnnualRevenueWithoutNoZeroNotSignificant() {
		GuiActionRunner.execute(() -> 
				balanceSwingView.setAnnualClientRevenue(CLIENT_FIXTURE_1,2019,300.55)
			);
		window.label("revenueLabel").requireText(
					"Il ricavo totale delle fatture del cliente "
							+CLIENT_FIXTURE_1.getIdentifier()+""
							+ " nel 2019 è di 300.55€");
	}
	
	@Test @GUITest
	public void testSetClientAnnualRevenueWithOneOrMoreZeroNotSignificant() {
		GuiActionRunner.execute(() -> 
			balanceSwingView.setAnnualClientRevenue(CLIENT_FIXTURE_1,2019,300.50)
		);
		window.label("revenueLabel").requireText(
				"Il ricavo totale delle fatture del cliente "
						+CLIENT_FIXTURE_1.getIdentifier()+""
						+ " nel 2019 è di 300.50€");
	}
	
	@Test @GUITest
	public void testClientRemovedShouldRemoveTheClientFromTheListAndCombobox(){
		GuiActionRunner.execute(() -> {
			DefaultListModel<Client> listClientsModel =balanceSwingView.getClientListModel();
			listClientsModel.addElement(CLIENT_FIXTURE_1);
			listClientsModel.addElement(CLIENT_FIXTURE_2);
			DefaultComboBoxModel<Client> comboboxClientsModel =balanceSwingView.getComboboxClientsModel();
			comboboxClientsModel.addElement(CLIENT_FIXTURE_1);
			comboboxClientsModel.addElement(CLIENT_FIXTURE_2);
			}
		);
		GuiActionRunner.execute( () -> balanceSwingView.clientRemoved(
				CLIENT_FIXTURE_1)
			);
		assertThat(window.list("clientsList").contents())
			.containsExactly(CLIENT_FIXTURE_2.toString());
		assertThat(window.comboBox("clientsCombobox").contents())
			.containsExactly(CLIENT_FIXTURE_2.toString());
	}
	
	@Test @GUITest
	public void testShowErrorClientShouldShowTheMessageInTheClientErrorLabel() {
		GuiActionRunner.execute(
				() -> balanceSwingView.showClientError("error message", CLIENT_FIXTURE_1) );
		window.label("labelClientErrorMessage").requireText("error message: " + 
				CLIENT_FIXTURE_1.getIdentifier());
	}
	
	@Test @GUITest
	public void testShowAllInvoicesButtonShouldBeVisibleOnlyWhenAClientIsSelected() {
		GuiActionRunner.execute(() -> balanceSwingView.getClientListModel()
				.addElement(CLIENT_FIXTURE_1)); 
		window.list("clientsList").selectItem(0); 
		JButtonFixture deleteButton = 
				window.button(JButtonMatcher.withText("Vedi tutte le fatture"));
		deleteButton.requireVisible();
		window.list("clientsList").clearSelection(); 
		deleteButton.requireNotVisible();
	}
	
	@Test @GUITest
	public void testShowAllInvoicesShouldDelegateToControllerFindAllInvoicesAndRevenue() {
		GuiActionRunner.execute(() -> {
			balanceSwingView.getClientListModel().addElement(CLIENT_FIXTURE_1);
			balanceSwingView.getComboboxYearsModel().addElement(CURRENT_YEAR);
			balanceSwingView.getComboboxYearsModel().addElement(YEAR_FIXTURE);
		}); 
		window.list("clientsList").selectItem(0);
		window.comboBox("yearsCombobox").selectItem(1);
		window.button(JButtonMatcher.withText("Vedi tutte le fatture")).click();
		verify(balanceController).allInvoicesByYear(YEAR_FIXTURE);
		verify(balanceController).annualRevenue(YEAR_FIXTURE);
		window.button(JButtonMatcher.withText("Vedi tutte le fatture")).requireNotVisible();
	}
	
}
