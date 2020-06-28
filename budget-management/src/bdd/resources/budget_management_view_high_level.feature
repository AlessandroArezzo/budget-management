Feature: Budget Management View High Level
	Specifications of the behavior for the budget management view
		
	Background:
		Given The database contains a few clients
		And The database contains a few invoices of different years
		And The Balance View is shown
		
	Scenario: The initial state of clients of the view
		Then The client list contains all clients contained in the database
		And The invoice list contains all invoices of the current year contained in the database
		And The initial total annual revenue of the current year is shown
	  
	Scenario: Show all invoices of a selected year
		When The user selects a year from the year selection combobox
		Then The invoice list contains all invoices of the selected year
		And The initial total annual revenue of the selected year is shown
		
	Scenario: Show invoices from a client of a selected year
		Given The user selects a year from the year selection combobox
		When The user selects a client from the client list
		Then The invoice list contains the invoices of the selected year for the client selected
		And The initial total annual revenue of the client selected of the selected year is shown
		
	Scenario: Show invoices of a selected year from a client 
		Given The user selects a client from the client list
		When The user selects a year from the year selection combobox
		Then The invoice list contains the invoices of the selected year for the client selected
		And The initial total annual revenue of the client selected of the selected year is shown
		
	Scenario: Show invoices of a client who has been removed from the database
		Given The user selects a year from the year selection combobox
		And A client is removed from the database
		When The user selects the client removed from the client list
		Then An error is shown containing the name of the selected client
		And The client is removed from the client list
		And The client is removed from client selection combobox in the invoice addition data
		And The invoice list contains all invoices of the selected year without invoices of the client removed
		And The total annual revenue of the selected year is shown considering invoices of the client removed 
			
	Scenario: Add a new client
		Given The user provides client data in the text fields 
		When The user clicks the "Aggiungi cliente" button
		Then The client list contains the new client
		And The client selection combobox in the invoice addition data contains the new client
		
	Scenario: Delete a client
		Given The user selects a year from the year selection combobox
		And The user selects a client from the client list
		When The user clicks the "Rimuovi cliente" button
		Then The client is removed from the client list
		And The client is removed from client selection combobox in the invoice addition data
		And The invoice list contains all invoices of the selected year without invoices of the client removed
	  And The total annual revenue of the selected year is shown considering invoices of the client removed
	  
	Scenario: Delete a client with no existing id
		Given The user selects a year from the year selection combobox
		And The user selects a client from the client list
		But The client is in meantime removed from the database
		When The user clicks the "Rimuovi cliente" button
		Then An error is shown containing the name of the selected client
		And The client is removed from the client list
		And The client is removed from client selection combobox in the invoice addition data
		And The invoice list contains all invoices of the selected year without invoices of the client removed
		And The total annual revenue of the selected year is shown considering invoices of the client removed
		
	
	Scenario: Show all invoices of a selected year after selected a client
		Given The user selects a year from the year selection combobox
		And The user selects a client from the client list
		When The user clicks the "Vedi tutte le fatture" button
		Then The invoice list contains all invoices of the selected year
		And The initial total annual revenue of the selected year is shown

	Scenario: Add a new invoice of the year selected in the year selection combobox
		Given The user selects a year from the year selection combobox
		And The user provides invoice data in the text fields with the same year as the one selected
		When The user clicks the "Aggiungi fattura" button
		Then The invoice list contains the new invoice
		And The total annual revenue of the selected year is updated also considering the new invoice added
		
	Scenario: Add a new invoice of a year other than the one selected in the year selection combobox
		Given The user selects a year from the year selection combobox 
		And The user provides invoice data in the text fields with year other than the one selected
		When The user clicks the "Aggiungi fattura" button
		Then The invoice list not contains the new invoice
		And The initial total annual revenue of the selected year is shown
		
	Scenario: Add a new invoice with no existing client in database
	  Given The user selects a year from the year selection combobox
		And The user provides invoice data in the text fields with the same year as the one selected
		But The client is in meantime removed from the database
		When The user clicks the "Aggiungi fattura" button
		Then An error is shown containing the name of the selected client
		And The client is removed from the client list
		And The invoice list contains all invoices of the selected year without invoices of the client removed
		And The total annual revenue of the selected year is shown considering invoices of the client removed
		
	Scenario: Delete an invoice
		Given The user selects a year from the year selection combobox
		And The user selects an invoice of the selected year from the invoice list
		When The user clicks the "Rimuovi fattura" button
		Then The invoice is removed from the invoice list
		And The total annual revenue of the selected year is shown considering the invoice removed
		
	Scenario: Delete an invoice with no existing id
		Given The user selects a year from the year selection combobox
		And The user selects an invoice of the selected year from the invoice list
		But The invoice is in meantime removed from the database
		When The user clicks the "Rimuovi fattura" button
		Then An error is shown containing the name of the selected invoice
		And The invoice is removed from the invoice list
		And The total annual revenue of the selected year is shown considering the invoice removed
	
	Scenario: Delete an invoice with no existing client
		Given The user selects a year from the year selection combobox
		And The user selects an invoice of the selected year from the invoice list
		But The client of invoice selected is in meantime removed from the database
		When The user clicks the "Rimuovi fattura" button
		Then An error is shown containing the name of the selected client
		And The client is removed from the client list
		And The client is removed from client selection combobox in the invoice addition data
		And The invoice list contains all invoices of the selected year without invoices of the client removed
		And The total annual revenue of the selected year is shown considering invoices of the client removed