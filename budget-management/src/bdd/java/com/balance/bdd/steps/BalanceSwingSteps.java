package com.balance.bdd.steps;

import javax.swing.JFrame;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.launcher.ApplicationLauncher;

import com.balance.model.Client;
import com.balance.model.Invoice;
import com.balance.utils.DateTestsUtil;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import static com.balance.bdd.steps.DatabaseSteps.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

public class BalanceSwingSteps {
	
	private FrameFixture window;
	
	private static final String NEW_CLIENT_IDENTIFIER="fourth identifier";
	
	@After 
	public void tearDown() {
		if (window != null) 
			window.cleanUp();
	}
	
	@Given("The Balance View is shown")
	public void the_Balance_View_is_shown() {
		ApplicationLauncher.application("com.balance.app.swing.BalanceSwingApp").withArgs(
				"--db-name="+ DB_NAME,
				"--collection-clients-name="+ COLLECTION_CLIENTS_NAME,
				"--collection-invoices-name="+ COLLECTION_INVOICES_NAME
				).start();
		window=WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "Budget Management View".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(BasicRobot.robotWithCurrentAwtHierarchy());
	}
	
	
	@Then("The client list contains all clients contained in the database")
	public void the_client_list_contains_all_clients_contained_in_the_database() {
		assertThat(window.list("clientsList").contents())
			.containsExactly(CLIENT_FIXTURE_1_IDENTIFIER,
					CLIENT_FIXTURE_2_IDENTIFIER,
					CLIENT_FIXTURE_3_IDENTIFIER
			);
	}
	
	@Then("The invoice list contains all invoices of the current year contained in the database")
	public void the_invoice_list_contains_all_invoices_of_the_current_year_contained_in_the_database() {
		assertThat(window.list("invoicesList").contents())
			.containsExactly(new Invoice(INVOICE_OF_THE_CURRENT_YEAR_1_CLIENT,
					INVOICE_OF_THE_CURRENT_YEAR_1_DATE, 
					INVOICE_OF_THE_CURRENT_YEAR_1_REVENUE).toString(),
				new Invoice(INVOICE_OF_THE_CURRENT_YEAR_2_CLIENT,INVOICE_OF_THE_CURRENT_YEAR_2_DATE,
						INVOICE_OF_THE_CURRENT_YEAR_2_REVENUE).toString()
		);
	}
	
	@Then("The initial total annual revenue of the current year is shown")
	public void the_initial_total_annual_revenue_of_the_current_year_is_shown() {
		window.label("revenueLabel").requireText(
				"Il ricavo totale del "+CURRENT_YEAR+" è di "+String.format("%.2f", 
					INVOICE_OF_THE_CURRENT_YEAR_1_REVENUE+INVOICE_OF_THE_CURRENT_YEAR_2_REVENUE)+"€");
	}
	

	@When("The user selects a year from the year selection combobox")
	public void the_user_selects_a_year_from_the_year_selection_combobox() {
		window.comboBox("yearsCombobox")
			.selectItem(Pattern.compile(""+YEAR_FIXTURE));
	}

	@Then("The invoice list contains all invoices of the selected year")
	public void the_invoice_list_contains_all_invoices_of_the_selected_year() {
		assertThat(window.list("invoicesList").contents())
			.containsExactly(
				new Invoice(INVOICE_OF_THE_YEAR_FIXTURE_1_CLIENT, INVOICE_OF_THE_YEAR_FIXTURE_1_DATE,
					INVOICE_OF_THE_YEAR_FIXTURE_1_REVENUE).toString(),
				new Invoice(INVOICE_OF_THE_YEAR_FIXTURE_2_CLIENT,INVOICE_OF_THE_YEAR_FIXTURE_2_DATE,
						INVOICE_OF_THE_YEAR_FIXTURE_2_REVENUE).toString(),
				new Invoice(INVOICE_OF_THE_YEAR_FIXTURE_3_CLIENT,INVOICE_OF_THE_YEAR_FIXTURE_3_DATE,
						INVOICE_OF_THE_YEAR_FIXTURE_3_REVENUE).toString()
		);
	}

	@Then("The initial total annual revenue of the selected year is shown")
	public void the_initial_total_annual_revenue_of_the_selected_year_is_shown() {
		window.label("revenueLabel").requireText(
				"Il ricavo totale del "+YEAR_FIXTURE+" è di "+String.format("%.2f", 
						INVOICE_OF_THE_YEAR_FIXTURE_1_REVENUE+INVOICE_OF_THE_YEAR_FIXTURE_2_REVENUE+
						INVOICE_OF_THE_YEAR_FIXTURE_3_REVENUE)+"€");
	}
	
	@Given("The user selects a client from the client list")
	public void the_user_selects_a_client_from_the_client_list() {
	    window.list("clientsList")
	    	.selectItem(Pattern.compile(CLIENT_FIXTURE_2_IDENTIFIER));
	}

	@Then("The invoice list contains the invoices of the selected year for the client selected")
	public void the_invoice_list_contains_the_invoices_of_the_selected_year_for_the_client_selected() {
		assertThat(window.list("invoicesList").contents())
		.containsExactly(
			new Invoice(INVOICE_OF_THE_YEAR_FIXTURE_2_CLIENT,INVOICE_OF_THE_YEAR_FIXTURE_2_DATE,
					INVOICE_OF_THE_YEAR_FIXTURE_2_REVENUE).toString(),
			new Invoice(INVOICE_OF_THE_YEAR_FIXTURE_3_CLIENT,INVOICE_OF_THE_YEAR_FIXTURE_3_DATE,
					INVOICE_OF_THE_YEAR_FIXTURE_3_REVENUE).toString()
		);
	}

	@Then("The initial total annual revenue of the client selected of the selected year is shown")
	public void the_initial_total_annual_revenue_of_the_client_selected_of_the_selected_year_is_shown() {
		window.label("revenueLabel").requireText(
				"Il ricavo totale delle fatture del cliente "+CLIENT_FIXTURE_2_IDENTIFIER
				+" nel "+YEAR_FIXTURE+" è di "+String.format("%.2f", 
						INVOICE_OF_THE_YEAR_FIXTURE_2_REVENUE+
						INVOICE_OF_THE_YEAR_FIXTURE_3_REVENUE)+"€");
	}

	@When("The user selects the client removed from the client list")
	public void the_user_selects_the_client_removed_from_the_client_list() {
		window.list("clientsList")
    		.selectItem(Pattern.compile(CLIENT_FIXTURE_2_IDENTIFIER));
	}

	@Then("An error is shown containing the name of the selected client")
	public void an_error_is_shown_containing_the_name_of_the_selected_client() {
		assertThat(window.label("labelClientErrorMessage").text())
			.contains(CLIENT_FIXTURE_2_IDENTIFIER); 
	}

	@Then("The client is removed from the client list")
	public void the_client_is_removed_from_the_client_list() {
		assertThat(window.list("clientsList").contents())
			.noneMatch(
				e -> e.contains(CLIENT_FIXTURE_2_IDENTIFIER));
	}

	@Then("The client is removed from client selection combobox in the invoice addition data")
	public void the_client_is_removed_from_client_selection_combobox_in_the_invoice_addition_data() {
		assertThat(window.comboBox("clientsCombobox").contents())
			.noneMatch(
				e -> e.contains(CLIENT_FIXTURE_2_IDENTIFIER));
	}

	@Then("The invoice list contains all invoices of the selected year without invoices of the client removed")
	public void the_invoice_list_contains_all_invoices_of_the_selected_year_without_invoices_of_the_client_removed() {
		assertThat(window.list("invoicesList").contents())
		.containsOnly(
			new Invoice(INVOICE_OF_THE_YEAR_FIXTURE_1_CLIENT,INVOICE_OF_THE_YEAR_FIXTURE_1_DATE,
					INVOICE_OF_THE_YEAR_FIXTURE_1_REVENUE).toString()
			);
	}

	@Then("The total annual revenue of the selected year is shown considering invoices of the client removed")
	public void the_total_annual_revenue_of_the_selected_year_is_shown_considering_invoices_of_the_client_removed() {
		window.label("revenueLabel").requireText(
				"Il ricavo totale del "+YEAR_FIXTURE+" è di "+String.format("%.2f", 
						INVOICE_OF_THE_YEAR_FIXTURE_1_REVENUE)+"€");
	}

	@Given("The user provides client data in the text fields")
	public void the_user_provides_client_data_in_the_text_fields() {
		window.textBox("textField_clientName").enterText(NEW_CLIENT_IDENTIFIER);
	}

	@When("The user clicks the {string} button")
	public void the_user_clicks_the_button(String string) {
		try {
			window.button(JButtonMatcher.withText(string)).click();
		}
		catch(ComponentLookupException e) {
			throw new cucumber.api.PendingException();
		}
	}

	@Then("The client list contains the new client")
	public void the_client_list_contains_the_new_client() {
		assertThat(window.list("clientsList").contents())
			.contains(NEW_CLIENT_IDENTIFIER);
	}

	@Then("The client selection combobox in the invoice addition data contains the new client")
	public void the_client_selection_combobox_in_the_invoice_addition_data_contains_the_new_client() {
		assertThat(window.comboBox("clientsCombobox").contents())
			.contains(NEW_CLIENT_IDENTIFIER);
	}

	@Given("The user provides invoice data in the text fields with the same year as the one selected")
	public void the_user_provides_invoice_data_in_the_text_fields_with_the_same_year_as_the_one_selected() {
		window.comboBox("clientsCombobox").selectItem(Pattern.compile(CLIENT_FIXTURE_2_IDENTIFIER));
		window.textBox("textField_dayOfDateInvoice").enterText("1");
		window.textBox("textField_monthOfDateInvoice").enterText("5");
		window.textBox("textField_yearOfDateInvoice").enterText(""+YEAR_FIXTURE);
		window.textBox("textField_revenueInvoice").enterText("100.25");
	}

	@Then("The invoice list contains the new invoice")
	public void the_invoice_list_contains_the_new_invoice() {
		assertThat(window.list("invoicesList").contents())
			.contains(new Invoice(new Client(CLIENT_FIXTURE_2_ID, CLIENT_FIXTURE_2_IDENTIFIER), 
					DateTestsUtil.getDate(1, 5, YEAR_FIXTURE), 100.25).toString());
	}

	@Then("The total annual revenue of the selected year is updated also considering the new invoice added")
	public void the_total_annual_revenue_of_the_selected_year_is_updated_also_considering_the_new_invoice_added() {
		window.label("revenueLabel").requireText(
				"Il ricavo totale del "+YEAR_FIXTURE+" è di "+String.format("%.2f", 
						INVOICE_OF_THE_YEAR_FIXTURE_1_REVENUE+INVOICE_OF_THE_YEAR_FIXTURE_2_REVENUE+
						INVOICE_OF_THE_YEAR_FIXTURE_3_REVENUE+
						100.25)+"€");
	}

	@Given("The user provides invoice data in the text fields with year other than the one selected")
	public void the_user_provides_invoice_data_in_the_text_fields_with_year_other_than_the_one_selected() {
		window.comboBox("clientsCombobox").selectItem(Pattern.compile(CLIENT_FIXTURE_2_IDENTIFIER));
		window.textBox("textField_dayOfDateInvoice").enterText("1");
		window.textBox("textField_monthOfDateInvoice").enterText("5");
		window.textBox("textField_yearOfDateInvoice").enterText(""+(YEAR_FIXTURE-1));
		window.textBox("textField_revenueInvoice").enterText("100.25");
	}

	@Then("The invoice list not contains the new invoice")
	public void the_invoice_list_not_contains_the_new_invoice() {
		assertThat(window.list("invoicesList").contents())
			.noneMatch(e -> e.contains(
				new Invoice(new Client(CLIENT_FIXTURE_2_ID, CLIENT_FIXTURE_2_IDENTIFIER), 
				DateTestsUtil.getDate(1, 5, YEAR_FIXTURE), 100.25).toString()));
	}

	@Given("The user selects an invoice of the selected year from the list")
	public void the_user_selects_an_invoice_of_the_selected_year_from_the_list() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new cucumber.api.PendingException();
	}

	@Then("The invoice is removed from the invoice list")
	public void the_invoice_is_removed_from_the_invoice_list() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new cucumber.api.PendingException();
	}

	@Then("The total annual revenue of the selected year is shown considering the invoice removed")
	public void the_total_annual_revenue_of_the_selected_year_is_shown_considering_the_invoice_removed() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new cucumber.api.PendingException();
	}

	@Given("The user selects an invoice of the selected year from the invoice list")
	public void the_user_selects_an_invoice_of_the_selected_year_from_the_invoice_list() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new cucumber.api.PendingException();
	}

	@Then("An error is shown containing the name of the selected invoice")
	public void an_error_is_shown_containing_the_name_of_the_selected_invoice() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new cucumber.api.PendingException();
	}



	
}
