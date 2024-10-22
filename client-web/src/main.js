// API Base URL
const API_URI = "http://localhost:8080";

// Error handling function
function onError(msg, error) {
    const out = `${msg}: ${error}`;
    console.error(out);
    // alert(out);
}

// Global variables
let domainSelected = '';
let should_render_domains = false;
let should_render_orders = false;
let user = {
    name: '',
    surname: '',
    email: ''
};

// DOM Elements
const checkDomainForm = document.getElementById('check-domain-form');
const purchaseDomainSection = document.getElementById('purchase-domain');
const buttonBuyDomain = document.getElementById('button-buy-domain');
const popupForm = document.getElementById('popup-form');
const closeBtn = document.querySelector('.close-btn');
const popupFormContent = document.getElementById('popup-form-content');
const showLoginBtn = document.getElementById('showLogin');
const showRegisterBtn = document.getElementById('showRegister');
const loginForm = document.getElementById('login-form');
const containerAccess = document.getElementById('container-access');
const containerCheckDomain = document.getElementById('container-check-domain');
const containerUserInfo = document.getElementById('container-user-information');
const showDomainList = document.getElementById('showDomainList');
const showOrderList = document.getElementById('showOrderList');
const domainTable = document.getElementById('domain-table');
const orderTable = document.getElementById('order-table');
const registerForm = document.getElementById('register-form');
const durationYearInput = document.getElementById('input-years-domain');

// Function to fetch user domains
async function getDomains(email) {
    const endpoint = `${API_URI}/domains/all/${email}`;

    try {
        let response = await fetch(endpoint);

        if (!response.ok) throw new Error(`Response from "${endpoint}" was not successful: ${response.status} ${response.statusText}`);

        return await response.json();
    } catch (error) {
        onError(`Failed to fetch domains from "${endpoint}"`, error);
    }
}

// Function to fetch user orders
async function getOrders(email) {
    const endpoint = `${API_URI}/orders/${email}`;

    try {
        let response = await fetch(endpoint);

        if (!response.ok) throw new Error(`Response from "${endpoint}" was not successful: ${response.status} ${response.statusText}`);

        return await response.json();
    } catch (error) {
        onError(`Failed to fetch orders from "${endpoint}"`, error);
    }
}

// Function to render user domain details
async function render_domain_details(event) {
    if (!should_render_domains) {
        orderTable.style.display = "none";
        domainTable.style.display = "none";
        return;
    }

    event.preventDefault();

    let domains = await getDomains(user.email);
    if (!domains.response || domains.response.length === 0) {
        alert("You have no domains");
    }

    if (domains && domains.response && domains.response.length > 0) {
        orderTable.style.display = "none";
        domainTable.style.display = "block";

        const tbody = document.getElementById("table-domain-body");
        tbody.innerHTML = "";

        domains.response.forEach(domain => {
            const row = tbody.insertRow();
            row.insertCell().innerText = domain.name;
            row.insertCell().innerText = parseLocalDateTime(domain.registration.registered_on);
            row.insertCell().innerText = parseLocalDateTime(domain.registration.expires_on);

            const actionsCell = row.insertCell();
            const renewButton = document.createElement("button");
            renewButton.type = "button";
            renewButton.innerText = "Renew";
            renewButton.style.width = 'auto';

            renewButton.addEventListener('click', () => {
                let maxRenewYears = getMaxRenewableYears(domain.registration.registered_on, domain.registration.expires_on);
                if (maxRenewYears === 0) {
                    alert("Cannot renew beyond 10 years");
                } else {
                    domainSelected = domain.name;
                    durationYearInput.max = maxRenewYears;
                    popupForm.style.display = 'flex';
                }
            });

            actionsCell.appendChild(renewButton);
        });
    } else {
        console.error("The response does not contain a valid array.");
    }
}

// Function to render user order details
async function render_order_details(event) {
    if (!should_render_orders) {
        orderTable.style.display = "none";
        domainTable.style.display = "none";
        return;
    }

    event.preventDefault();

    let orders = await getOrders(user.email);
    if (!orders.response || orders.response.length === 0) {
        alert("You have no orders");
    }

    if (orders && orders.response && orders.response.length > 0) {
        orderTable.style.display = "block";
        domainTable.style.display = "none";

        const tbody = document.getElementById("table-order-body");
        tbody.innerHTML = "";

        orders.response.forEach(order => {
            const row = tbody.insertRow();
            row.insertCell().innerText = order.referring_to.name;
            row.insertCell().innerText = parseLocalDateTime(order.ordered_on);
            row.insertCell().innerText = order.type === 0 ? "Register" : "Renewal";
            row.insertCell().innerText = order.payment_amount;
        });
    } else {
        console.error("The response does not contain a valid array.");
    }
}

// Function to show the domain check and user information sections
function showServices() {
    containerAccess.style.display = 'none';
    containerCheckDomain.style.display = 'block';
    containerUserInfo.style.display = 'block';
}

// Function to fetch user information by email
async function getUser(email) {
    const endpoint = `${API_URI}/users/${email}`;
    try {
	const response = await fetch(endpoint, { method: "GET" });
	if (!response.ok) {
	    const errorResponse = await response.json();
	    if (errorResponse.reason === "UserDoesNotExists") {
		throw new Error('User not found');
	    }
	    throw new Error(`Response was not successful: ${response.status} ${response.statusText}`);
	}
	return await response.json();
    } catch (error) {
	onError("Error fetching user information", error);
	throw error;
    }
}

// Event listener for login form submission
loginForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    const email = document.getElementById('login-email').value;
    try {
	const result = await getUser(email);
	if (result.name && result.surname) {
	    user.name = result.name;
	    user.surname = result.surname;
	    user.email = email;
	    showServices();
	} else {
	    throw new Error("Incomplete user information");
	}
    } catch (error) {
	if (error.message === 'User not found') {
	    alert('User not registered');
	}
    }
});

// Function to register a new user
async function newUser(user) {
    const endpoint = `${API_URI}/users/register`;
    try {
	const response = await fetch(endpoint, {
	    method: "POST",
	    headers: { "Content-Type": "application/json" },
	    body: JSON.stringify(user)
	});

	if (!response.ok) {
	    const errorResponse = await response.json();
	    if (errorResponse.reason === "UserAlreadyExists") {
		throw new Error('User already exists');
	    }
	    throw new Error(`Response was not successful: ${response.status} ${response.statusText}`);
	}
    } catch (error) {
	onError("Error registering user", error);
	throw error;
    }
}

// Event listener for the registration form submission
registerForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    user.name = document.getElementById('name').value;
    user.surname = document.getElementById('surname').value;
    user.email = document.getElementById('register-email').value;
    try {
	await newUser(user);
	showServices();
    } catch (error) {
	console.error('Error registering user:', error);
    }
});

// Function to check domain availability
async function checkDomain(domain) {
    const endpoint = `${API_URI}/domains/${domain}`;

    try {
        const response = await fetch(endpoint, { method: "GET" });

        if (!response.ok) throw new Error(`Response from "${endpoint}" was not successful: ${response.status} ${response.statusText}`);

        return response.json();
    } catch (error) {
        onError(`Failed to check domain "${domain}"`, error);
    }
}

// Event listener for the domain check form submission
checkDomainForm.addEventListener('submit', async function(event) {
    event.preventDefault();

    const domainInput = document.getElementById('input-domain').value.trim();
    const domainPattern = /^[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    if (!domainPattern.test(domainInput)) {
        alert('Please enter a valid domain name');
        return;
    }

    console.log("checking domain");
    var domain = domainInput;

    try {
        const result = await checkDomain(domain);
        console.log('Domain checked:', result);

        // Remove any existing messages and tables
        const existingMessage = document.getElementById('unavailable-message');
        if (existingMessage) existingMessage.remove();

        const existingTable = document.getElementById('user-info-table');
        if (existingTable) existingTable.remove();

        if (result.available) {
            purchaseDomainSection.style.display = "block";
        } else {
            purchaseDomainSection.style.display = "none";
            displayDomainUnavailable(result);
        }
    } catch (error) {
        console.error('Error checking domain:', error);
    }
});


// Display domain unavailable message and owner information
function displayDomainUnavailable(result) {
  let text = document.createElement("span");
  text.id = 'unavailable-message';

  if (result.owner.email === user.email) {
      text.innerText = "Owned by you!";
      checkDomainForm.appendChild(text);
  } else {
      text.innerText = "Domain not available!";
      text.style.color = "red";
      text.style.marginTop = "10px";
      
      let table = document.createElement("table");
      table.id = 'user-info-table';
      let caption = table.createCaption();
      caption.innerText = "Purchased by";
      
      let tbody = document.createElement("tbody");
      addTableRow(tbody, "Name:", result.owner.name);
      addTableRow(tbody, "Surname:", result.owner.surname);
      addTableRow(tbody, "Email:", result.owner.email);
      addTableRow(tbody, "Registered:", result.registered_on.split('T')[0]);
      addTableRow(tbody, "Expires:", result.expires_on.split('T')[0]);
      
      table.appendChild(tbody);
      checkDomainForm.appendChild(text);
      checkDomainForm.appendChild(table);
  }
}

// Add a row to the table
function addTableRow(tbody, label, value) {
  let row = tbody.insertRow();
  row.insertCell().innerText = label;
  row.insertCell().innerText = value;
}


// Function to make an order
async function makeOrder(orderDetails) {
    const endpoint = `${API_URI}/orders`;

    try {
        const response = await fetch(endpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(orderDetails)
        });

        if (!response.ok) {
            const errorResponse = await response.json();

            if (response.status === 400 && errorResponse.reason === "DomainOwnedByDifferentUser") {
                alert("Cannot proceed with the order, the domain has been purchased by someone else");
            }

            throw new Error(errorResponse.message || `Response from "${endpoint}" was not successful: ${response.status} ${response.statusText}`);
        }

        return response.json();
    } catch (error) {
        console.error('Error making order:', error);
        throw error;
    }
}

// Event listener for the purchase form submission
purchaseDomainSection.addEventListener('submit', (event) => {
    event.preventDefault();
    popupForm.style.display = 'flex';
});

// Close popup
closeBtn.addEventListener('click', () => {
    popupForm.style.display = 'none';
});

// Event listener for the payment form submission
popupFormContent.addEventListener('submit', async function(event) {
    event.preventDefault();

    const orderDetails = {
        name: user.name,
        surname: user.surname,
        email: user.email,
        duration: document.getElementById('input-years-domain').value,
        cardOwner: document.getElementById('card-owner').value,
        cardNumber: document.getElementById('card-number').value,
        cardExpiration: document.getElementById('card-expiration').value,
        cardCVV: document.getElementById('card-cvv').value,
        domain: document.getElementById('input-domain').value || domainSelected
    };

    try {
        await makeOrder(orderDetails);
        alert("Purchase successful");
        purchaseDomainSection.style.display = "none";
	popupForm
        render_domain_details(event);
        render_order_details(event);
    } catch (error) {
        console.error('Error processing order:', error);
    } finally {
	popupForm.style.display = 'none';
	popupFormContent.reset();
    }
});

// Function to parse date to local date time
function parseLocalDateTime(str) {
    return new Date(Date.parse(str)).toString().split('GMT')[0];
}

// Function to get max renewable years for a domain
function getMaxRenewableYears(expiration_str, registration_str) {
    let registration = new Date(Date.parse(registration_str)).getFullYear();
    let expiration = new Date(Date.parse(expiration_str)).getFullYear();

    return 10 + (expiration - registration);
}

// Event listener to toggle domain list visibility
showDomainList.addEventListener('click', function(event) {
    should_render_orders = false;
    should_render_domains = !should_render_domains;
    render_domain_details(event);
});

// Event listener to toggle order list visibility
showOrderList.addEventListener('click', function(event) {
    should_render_domains = false;
    should_render_orders = !should_render_orders;
    render_order_details(event);
});

// Toggle login and register forms
showLoginBtn.addEventListener('click', () => {
    loginForm.classList.remove('hidden');
    registerForm.classList.add('hidden');
    showLoginBtn.classList.add('hidden');
    showRegisterBtn.classList.remove('hidden');
});

showRegisterBtn.addEventListener('click', () => {
    loginForm.classList.add('hidden');
    registerForm.classList.remove('hidden');
    showRegisterBtn.classList.add('hidden');
    showLoginBtn.classList.remove('hidden');
});
