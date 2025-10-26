
// =============================
// PolicyHub Admin Dashboard JS
// =============================
const API_BASE = "http://localhost:8082/api";

document.addEventListener("DOMContentLoaded", async () => {
  await new Promise(resolve => setTimeout(resolve, 300));
  const isAdmin = await verifyAdminLogin();
  if (!isAdmin) {
    alert("Please login as admin first!");
    window.location.href = "index.html";
    return;
  }

  showSection("dashboard");
  loadDashboard();
  loadPolicies();
  loadCustomers();
  loadClaims();
  loadPremiums();
  setupAddPolicyForm();
});

// ---------------- Navigation ----------------
function showSection(section) {
  const sections = ["dashboard", "policy", "claim", "premium", "customer"];
  sections.forEach((s) => document.getElementById(`${s}Section`)?.classList.add("d-none"));
  document.getElementById(`${section}Section`)?.classList.remove("d-none");
}

// ---------------- Dashboard Data ----------------

async function loadDashboard() {
  try {
    const res = await fetch(`${API_BASE}/dashboard/stats`, { credentials: "include" });
    if (!res.ok) {
      console.error("Dashboard stats failed:", res.status);
      alert("Unable to load dashboard data. Please login again as admin.");
      return;
    }

    const data = await res.json();
    // Assuming your HTML has IDs: totalCustomers, totalPolicies, totalClaims, totalRevenue
    document.getElementById("totalCustomers").innerText = data.customers;
    document.getElementById("totalPolicies").innerText = data.policies;
    document.getElementById("totalClaims").innerText = data.claims;
    document.getElementById("totalRevenue").innerText = "₹" + parseFloat(data.revenue).toFixed(2);
  } catch (err) {
    console.error("Dashboard stats load error:", err);
    alert("Could not fetch dashboard data. Check backend connection.");
  }
}


// ---------------- Policies ----------------
async function loadPolicies() {
  const tbody = document.getElementById("policyTable");
  tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">Loading...</td></tr>`;
  try {
    const res = await fetch(`${API_BASE}/policies`, { credentials: "include" });
    const data = await res.json();
    if (!data.length) {
      tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No policies found</td></tr>`;
      return;
    }
    tbody.innerHTML = data.map(p => `
      <tr>
        <td>${p.policyId}</td>
        <td>${p.policyType}</td>
        <td>₹${p.coverageAmount}</td>
        <td>${p.durationYears} yrs</td>        
        <td>₹${p.premiumAmount || "-"}</td>
        <td>${p.validityStartDate}</td>
        <td>${p.validityEndDate}</td>

        <td>
          <button class="btn btn-sm btn-outline-primary me-1" onclick="editPolicy(${p.policyId})">Edit<i class="fas fa-edit"></i></button>
          <button class="btn btn-sm btn-outline-danger" onclick="deletePolicy(${p.policyId})">Delete<i class="fas fa-trash"></i></button>
        </td>
      </tr>`).join("");
  } catch (err) {
    console.error("Policy load error:", err);
  }
}

function setupAddPolicyForm() {
  const form = document.getElementById("policyForm");
  form.onsubmit = async (e) => {
    e.preventDefault();
    const type = document.getElementById("type").value.trim();
    const coverage = parseFloat(document.getElementById("coverage").value);
    const duration = parseInt(document.getElementById("duration").value);

    // if (!type || coverage <= 0 || duration <= 0) {
    //   alert(" Please enter valid details.");
    //   return;
    // }

    const payload = { policyType: type, coverageAmount: coverage, durationYears: duration };

    try {
      const res = await fetch(`${API_BASE}/policies/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(payload),
      });

      if (res.ok) {
        alert(" Policy added successfully!");
        form.reset();
        loadPolicies();
        loadDashboard();
        bootstrap.Modal.getInstance(document.getElementById("addPolicyModal")).hide();
      } else {
        alert("Failed to add policy.");
      }
    } catch (err) {
      console.error("Add policy error:", err);
    }
  };
}

async function deletePolicy(id) {
  if (!confirm("Delete this policy?")) return;
  try {
    const res = await fetch(`${API_BASE}/policies/${id}`, {
      method: "DELETE",
      credentials: "include"
    });

    if (res.ok) {
      alert("Policy deleted successfully!");
      loadPolicies();
      loadDashboard();
    } else {
      const msg = await res.text();
      alert("Failed to delete policy: " + msg);
    }
  } catch (err) {
    console.error("Error deleting policy:", err);
    alert("Network error while deleting policy.");
  }
}

async function editPolicy(id) {
  try {
    const res = await fetch(`${API_BASE}/policies/${id}`, { credentials: "include" });
    const p = await res.json();

    // Fill modal fields
    document.getElementById("type").value = p.policyType;
    document.getElementById("coverage").value = p.coverageAmount;
    document.getElementById("duration").value = p.durationYears;

    const form = document.getElementById("policyForm");
    const modal = new bootstrap.Modal(document.getElementById("addPolicyModal"));
    modal.show();

    // Remove any old event handler
    form.onsubmit = null;

    form.onsubmit = async (e) => {
      e.preventDefault();

      const payload = {
        policyType: document.getElementById("type").value.trim(),
        coverageAmount: parseFloat(document.getElementById("coverage").value),
        durationYears: parseInt(document.getElementById("duration").value)
      };

      try {
        const updateRes = await fetch(`${API_BASE}/policies/${id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
          credentials: "include"
        });

        if (updateRes.ok) {
          alert("Policy updated successfully!");
          modal.hide();
          loadPolicies();
          loadDashboard();
        } else {
          const err = await updateRes.text();
          alert("Update failed: " + err);
        }
      } catch (err) {
        console.error(err);
        alert("Network error updating policy.");
      }
    };
  } catch (err) {
    console.error("Edit load failed:", err);
    alert("Failed to load policy details.");
  }




}

// ---------------- Customers ----------------
async function loadCustomers() {
  const tbody = document.getElementById("customerTable");
  const res = await fetch(`${API_BASE}/customers`, { credentials: "include" });
  const data = await res.json();
  tbody.innerHTML = data.map(c => `
    <tr><td>${c.customerId}</td><td>${c.fullName}</td><td>${c.email}</td><td>${c.phone}</td><td>${c.address}</td></tr>
  `).join("");
}

// ---------------- Claims ----------------
async function loadClaims() {
  const tbody = document.getElementById("claimTable");
  tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">Loading...</td></tr>`;

  try {
    const res = await fetch(`${API_BASE}/claims`, { credentials: "include" });
    const data = await res.json();

    if (!data.length) {
      tbody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No claims available.</td></tr>`;
      return;
    }

    tbody.innerHTML = data.map(c => `
      <tr>
        <td>${c.claimId}</td>
        <td>${c.customer?.fullName || "-"}</td>
        <td>${c.policy?.policyType || "-"}</td>
        <td>₹${c.claimAmount}</td>
        <td>${c.claimStatus}</td>
        <td>
          ${
            c.claimStatus === "PENDING"
              ? `<button class="btn btn-sm btn-success me-1" onclick="updateClaimStatus(${c.claimId}, 'APPROVED')">
                   Approve
                 </button>
                 <button class="btn btn-sm btn-danger" onclick="updateClaimStatus(${c.claimId}, 'REJECTED')">
                   Reject
                 </button>`
              : `<span class="text-muted">No Actions</span>`
          }
        </td>
      </tr>
    `).join("");
  } catch (err) {
    console.error("Error loading claims:", err);
    tbody.innerHTML = `<tr><td colspan="6" class="text-center text-danger">Failed to load claims.</td></tr>`;
  }
}

async function updateClaimStatus(id, status) {
  try {
    const res = await fetch(`${API_BASE}/claims/${id}/status?status=${status}`, {
      method: "PUT",
      credentials: "include"
    });

    if (res.ok) {
      alert(`Claim ${status} successfully!`);
      loadClaims(); // Refresh claims list
      loadDashboard(); // Refresh stats
    } else {
      const msg = await res.text();
      alert("Failed: " + msg);
    }
  } catch (err) {
    console.error("Update claim failed:", err);
    alert("Network error while updating claim status.");
  }
}

// ---------------- Premiums ----------------
async function loadPremiums() {
  const tbody = document.getElementById("premiumTable");
  const res = await fetch(`${API_BASE}/premiums`, { credentials: "include" });
  const data = await res.json();
  tbody.innerHTML = data.map(p => `
    <tr>
      <td>${p.premiumId}</td>
      <td>${p.customer?.fullName || "-"}</td>
      <td>${p.policy?.policyType || "-"}</td>
      <td>₹${p.amount}</td>
      <td>${p.status}</td>
    </tr>
  `).join("");
}

// ---------------- Auth ----------------
async function verifyAdminLogin() {
  try {
    const res = await fetch(`${API_BASE}/auth/current`, { credentials: "include" });
    if (res.ok) {
      const user = await res.json();
      return user.role === "ADMIN";
    }
  } catch {
    return false;
  }
  return false;
}

async function logoutAdmin() {
  await fetch(`${API_BASE}/auth/logout`, { method: "POST", credentials: "include" });
  alert("Logged out successfully!");
  window.location.href = "index.html";
}
