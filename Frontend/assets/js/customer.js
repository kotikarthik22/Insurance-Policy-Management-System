// ==========================
// CUSTOMER DASHBOARD SCRIPT
// ==========================

const API_BASE = "http://localhost:8082/api"; // adjust port if needed

// All section IDs in UI
const sections = ["dashboard", "available", "mypolicies", "claims", "premium", "profile"];

// -------- Show & Hide Sections --------
function showSection(id) {
  sections.forEach((s) => {
    document.getElementById(`${s}Section`).classList.add("d-none");
  });
  document.getElementById(`${id}Section`).classList.remove("d-none");

  const titles = {
    dashboard: "Customer Dashboard",
    available: "Available Policies",
    mypolicies: "My Policies",
    claims: "My Claims",
    premium: "Premium Payments",
    profile: "My Profile",
  };

  document.getElementById(
    "pageTitle"
  ).innerHTML = `<i class="fas fa-layer-group text-warning me-2"></i>${titles[id]}`;
}

// -------- Logout --------
function logoutCustomer() {
  fetch(`${API_BASE}/auth/logout`, { method: "POST", credentials: "include" })
    .then(() => (window.location.href = "index.html"))
    .catch((err) => console.error("Logout failed:", err));
}

// ====================================
// DASHBOARD OVERVIEW STATS
// ====================================
async function loadDashboardStats() {
  try {
    const [policyRes, claimRes, premiumRes] = await Promise.all([
      fetch(`${API_BASE}/customers/policies`, { credentials: "include" }),
      fetch(`${API_BASE}/customers/claims`, { credentials: "include" }),
      fetch(`${API_BASE}/customers/premiums`, { credentials: "include" }),
    ]);

    const policies = await policyRes.json();
    const claims = await claimRes.json();
    const premiums = await premiumRes.json();

    document.querySelectorAll(".card h4")[0].innerText = policies.length;
    document.querySelectorAll(".card h4")[1].innerText = claims.length;
    document.querySelectorAll(".card h4")[2].innerText =
      "₹" + premiums.reduce((sum, p) => sum + (p.amount || 0), 0);
  } catch (err) {
    console.error("Error loading dashboard stats:", err);
  }
}

// ====================================
// AVAILABLE POLICIES
// ====================================
async function loadAvailablePolicies() {
  try {
    const res = await fetch(`${API_BASE}/policies`, { credentials: "include" });
    const data = await res.json();

    const table = document.getElementById("availablePoliciesTable");
    table.innerHTML = "";

    if (!data.length) {
      table.innerHTML =
        "<tr><td colspan='5' class='text-center text-muted'>No policies available.</td></tr>";
      return;
    }

    data.forEach((p) => {
      table.innerHTML += `
        <tr>
          <td>${p.policyId}</td>
          <td>${p.policyType}</td>
          <td>₹${p.coverageAmount}</td>
          <td>₹${p.premiumAmount}</td>
          <td>
            <button class="btn btn-sm btn-success" onclick="buyPolicy(${p.policyId})">
              <i class="fas fa-cart-plus me-1"></i>Buy
            </button>
          </td>
        </tr>`;
    });
  } catch (err) {
    console.error("Failed to load available policies:", err);
  }
}

// Buy Policy
async function buyPolicy(policyId) {
  if (!confirm("Do you want to buy this policy?")) return;

  const res = await fetch(`${API_BASE}/customers/${window.customerId}/buy/${policyId}`, {
    method: "POST",
    credentials: "include",
  });

  if (res.ok) {
    alert("Policy purchased successfully!");
    loadDashboardStats();
    loadAvailablePolicies();
    loadMyPolicies();
    loadPremiums();
  } else {
    alert("Failed to buy policy. Try again.");
  }
}

// ====================================
// MY POLICIES
// ====================================
async function loadMyPolicies() {
  const res = await fetch(`${API_BASE}/customers/policies`, {
    credentials: "include",
  });
  const data = await res.json();

  const table = document.getElementById("myPoliciesTable");
  table.innerHTML = "";

  if (!data.length) {
    table.innerHTML =
      "<tr><td colspan='6' class='text-center text-muted'>No policies purchased yet.</td></tr>";
    return;
  }

  data.forEach((p) => {
    table.innerHTML += `
      <tr>
        <td>${p.policyId}</td>
        <td>${p.policyType}</td>
        <td>₹${p.coverageAmount}</td>
        <td>₹${p.premiumAmount}</td>
        <td>${p.validityStartDate}</td>
        <td>${p.validityEndDate}</td>
        <td>
          <button class="btn btn-sm btn-outline-primary" onclick="openClaimModal(${p.policyId})">
            <i class="fas fa-file-medical me-1"></i> Claim
          </button>
        </td>
      </tr>`;
  });
}

// ====================================
// CLAIMS
// ====================================
async function loadClaims() {
  const res = await fetch(`${API_BASE}/customers/claims`, {
    credentials: "include",
  });
  const data = await res.json();

  const table = document.getElementById("claimsTable");
  table.innerHTML = "";

  if (!data.length) {
    table.innerHTML =
      "<tr><td colspan='5' class='text-center text-muted'>No claims filed yet.</td></tr>";
    return;
  }

  data.forEach((c) => {
    table.innerHTML += `
      <tr>
        <td>${c.claimId}</td>
        <td>${c.policy?.policyType || "N/A"}</td>
        <td>₹${c.claimAmount}</td>
        <td>${c.submissionDate}</td>
        <td>${c.claimStatus}</td>
        <td>
          ${
            c.claimStatus === "PENDING"
              ? `<button class="btn btn-sm btn-primary" onclick="viewClaim(${c.claimId})">
                    <i class="fas fa-eye"></i> View
                 </button>`
              : "No Actions"
          }
        </td>
      </tr>`;
  });
}

// View Claim
async function viewClaim(claimId) {
  const res = await fetch(`${API_BASE}/claims/${claimId}`, {
    credentials: "include",
  });
  const claim = await res.json();

  alert(`Claim Details:\nID: ${claim.claimId}\nPolicy: ${claim.policy?.policyType || "N/A"}\nAmount: ₹${claim.claimAmount}\nStatus: ${claim.claimStatus}\nDescription: ${claim.description || "N/A"}`);
}

// ====================================
// PREMIUMS
// ====================================
async function loadPremiums() {
  const res = await fetch(`${API_BASE}/customers/premiums`, {
    credentials: "include",
  });
  const data = await res.json();

  const table = document.getElementById("premiumTable");
  table.innerHTML = "";

  if (!data.length) {
    table.innerHTML =
      "<tr><td colspan='6' class='text-center text-muted'>No premium data available.</td></tr>";
    return;
  }

  data.forEach((p) => {
    table.innerHTML += `
      <tr>
        <td>${p.premiumId}</td>
        <td>${p.policy?.policyType || "N/A"}</td>
        <td>₹${p.amount}</td>
        <td>${p.status}</td>
        <td>${p.dueDate}</td>
        <td>
          ${p.status === "PENDING"
            ? `<button class="btn btn-sm btn-success" onclick="payPremium(${p.premiumId})">
                 <i class="fas fa-credit-card me-1"></i>Pay
               </button>`
            : `<span class="text-muted">Paid</span>`}
        </td>
      </tr>`;
  });
}

// Pay Premium
async function payPremium(premiumId) {
  if (!confirm("Do you want to pay this premium?")) return;

  const res = await fetch(`${API_BASE}/premiums/${premiumId}/pay`, {
    method: "PUT",
    credentials: "include",
  });

  if (res.ok) {
    alert("Premium paid successfully!");
    loadPremiums();
    loadDashboardStats();
  } else {
    alert("Failed to pay premium. Try again.");
  }
}

// ====================================
// Claim model
// ====================================


function openClaimModal(policyId) {
  document.getElementById("claimPolicyId").value = policyId;
  const modal = new bootstrap.Modal(document.getElementById("claimModal"));
  modal.show();
}

document.getElementById("claimForm").addEventListener("submit", async (e) => {
  e.preventDefault();

  const policyId = document.getElementById("claimPolicyId").value;
  const amount = document.getElementById("claimAmount").value;

  if (!amount || amount <= 0) {
    alert("Please enter a valid claim amount.");
    return;
  }

  try {
    // Get customer profile to get customerId
    const profileRes = await fetch(`${API_BASE}/customers/profile`, { credentials: "include" });
    const customer = await profileRes.json();

    const res = await fetch(`${API_BASE}/claims/customer/${customer.customerId}/policy/${policyId}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ claimAmount: parseFloat(amount) }),
      credentials: "include"
    });

    if (res.ok) {
      alert("Claim submitted successfully!");
      bootstrap.Modal.getInstance(document.getElementById("claimModal")).hide();
      loadClaims(); // reload claims section
    } else {
      const msg = await res.text();
      alert("Claim failed: " + msg);
    }
  } catch (err) {
    console.error(err);
    alert("Network error while submitting claim.");
  }
});


// ====================================
// PROFILE
// ====================================
async function loadProfile() {
  const res = await fetch(`${API_BASE}/customers/profile`, {
    credentials: "include",
  });
  const data = await res.json();

  document.getElementById("profName").innerText = data.fullName;
  document.getElementById("profEmail").innerText = data.email;
  document.getElementById("profPhone").innerText = data.phone;
  document.getElementById("profAddress").innerText = data.address;

  document.getElementById("welcomeUser").innerText = `Welcome, ${data.fullName}`;

  // Store customerId for later use
  window.customerId = data.customerId;
}

// ====================================
// INITIAL LOAD
// ====================================
document.addEventListener("DOMContentLoaded", () => {
  loadDashboardStats();
  loadAvailablePolicies();
  loadMyPolicies();
  loadClaims();
  loadPremiums();
  loadProfile();
});
